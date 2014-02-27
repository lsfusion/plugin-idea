package com.lsfusion.psi;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.util.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import com.intellij.reference.SoftReference;
import com.intellij.util.containers.ConcurrentWeakHashMap;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentMap;

public class LSFResolveCache {
    private static final LSFResolveCache VALUE_CLASS_INSTANCE = new LSFResolveCache();
    private static final LSFResolveCache PARAM_CLASSES_INSTANCE = new LSFResolveCache();

    private static final Logger LOG = Logger.getInstance("#com.lsfusion.psi.LSFResolveCache");
    private final ConcurrentMap[] myMaps = new ConcurrentMap[2 * 2]; //boolean physical, boolean incompleteCode
    private final RecursionGuard myGuard = RecursionManager.createGuard("lsfResolveCache");

    public interface AbstractResolver<TRef extends LSFElement, TResult> {
        TResult resolve(@NotNull TRef ref, boolean incompleteCode);
    }

    public LSFResolveCache() {
        for (int i = 0; i < myMaps.length; i++) {
            myMaps[i] = new ConcurrentWeakHashMap(100, 0.75f, Runtime.getRuntime().availableProcessors(), ContainerUtil.canonicalStrategy());
        }
    }

    public static LSFResolveCache getValueClassInstance() {
        ProgressIndicatorProvider.checkCanceled();
        return VALUE_CLASS_INSTANCE;
    }

    public static LSFResolveCache getParamClassesInstance() {
        ProgressIndicatorProvider.checkCanceled();
        return PARAM_CLASSES_INSTANCE;
    }

    public void clearCache(boolean isPhysical) {
        int startIndex = isPhysical ? 0 : 1;
        for (int i = startIndex; i < 2; i++) for (int j = 0; j < 2; j++) myMaps[i * 2 + j].clear();
    }

    @Nullable
    private <TRef extends LSFElement, TResult> TResult resolve(@NotNull final TRef ref,
                                                               @NotNull final AbstractResolver<TRef, TResult> resolver,
                                                               boolean needToPreventRecursion,
                                                               final boolean incompleteCode,
                                                               boolean isPhysical) {
        ProgressIndicatorProvider.checkCanceled();
        ApplicationManager.getApplication().assertReadAccessAllowed();

        ConcurrentMap<TRef, Getter<TResult>> map = getMap(isPhysical, incompleteCode);
        Getter<TResult> reference = map.get(ref);
        TResult result = reference == null ? null : reference.get();
        if (result != null) {
            return result;
        }

        RecursionGuard.StackStamp stamp = myGuard.markStack();
        result = needToPreventRecursion ? myGuard.doPreventingRecursion(Pair.create(ref, incompleteCode), true, new Computable<TResult>() {
            @Override
            public TResult compute() {
                return resolver.resolve(ref, incompleteCode);
            }
        }) : resolver.resolve(ref, incompleteCode);
        PsiElement element = result instanceof ResolveResult ? ((ResolveResult) result).getElement() : null;
        LOG.assertTrue(element == null || element.isValid(), result);

        if (stamp.mayCacheNow()) {
            cache(ref, map, result);
        }
        return result;
    }

    @Nullable
    public <TRef extends LSFElement, TResult>
    TResult resolveWithCaching(@NotNull TRef ref,
                               @NotNull AbstractResolver<TRef, TResult> resolver,
                               boolean needToPreventRecursion,
                               boolean incompleteCode) {
        return resolve(ref, resolver, needToPreventRecursion, incompleteCode, ref.isPhysical());
    }

    private <TRef extends LSFElement, TResult> ConcurrentMap<TRef, Getter<TResult>> getMap(boolean physical, boolean incompleteCode) {
        //noinspection unchecked
        return myMaps[(physical ? 0 : 1) * 2 + (incompleteCode ? 0 : 1)];
    }

    private static class SoftGetter<T> extends SoftReference<T> implements Getter<T> {
        public SoftGetter(T referent) {
            super(referent);
        }
    }

    private static final Getter<Object> NULL_RESULT = new StaticGetter<Object>(null);

    private static <TRef extends LSFElement, TResult> void cache(@NotNull TRef ref,
                                                                 @NotNull ConcurrentMap<TRef, Getter<TResult>> map,
                                                                 TResult result) {
        // optimization: less contention
        Getter<TResult> cached = map.get(ref);
        if (cached != null && cached.get() == result) {
            return;
        }
        if (result == null) {
            // no use in creating SoftReference to null
            //noinspection unchecked
            cached = (Getter<TResult>) NULL_RESULT;
        } else {
            cached = new SoftGetter<TResult>(result);
        }
        map.put(ref, cached);
    }
}
