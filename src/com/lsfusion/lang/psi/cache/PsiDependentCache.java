package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.util.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.AnyPsiChangeListener;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.reference.SoftReference;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentMap;

public abstract class PsiDependentCache<Psi extends PsiElement, TResult> {
    private static final Logger LOG = Logger.getInstance("#com.lsfusion.lang.psi.cache.LSFResolveCache");

    private final ConcurrentMap<Psi, Getter<TResult>>[] myMaps = new ConcurrentMap[2 * 2]; //boolean physical, boolean incompleteCode
    private final RecursionGuard myGuard = RecursionManager.createGuard("lsfResolveCache");

    public interface PsiResolver<Psi extends PsiElement, TResult> {
        TResult resolve(@NotNull Psi psi, boolean incompleteCode);
        
        boolean checkResultClass(Object result); // например при memoize в RecursionGuard возвращается List чего-то
    }

    public PsiDependentCache(@NotNull MessageBus messageBus) {
        for (int i = 0; i < myMaps.length; i++) {
            myMaps[i] = ContainerUtil.createConcurrentWeakMap(100, 0.75f, Runtime.getRuntime().availableProcessors(), ContainerUtil.<Psi>canonicalStrategy());
        }
        messageBus.connect().subscribe(PsiManagerImpl.ANY_PSI_CHANGE_TOPIC, new AnyPsiChangeListener() {
            @Override
            public void beforePsiChanged(boolean isPhysical) {
                clearCache(isPhysical);
            }

            @Override
            public void afterPsiChanged(boolean isPhysical) {
            }
        });
    }

    private void clearCache(boolean isPhysical) {
        int startIndex = isPhysical ? 0 : 1;
        for (int i = startIndex; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                myMaps[i * 2 + j].clear();
            }
        }
    }

    @Nullable
    public TResult resolveWithCaching(@NotNull Psi element, @NotNull PsiResolver<Psi, TResult> resolver, boolean needToPreventRecursion, boolean incompleteCode) {
        return resolve(element, resolver, needToPreventRecursion, incompleteCode, element.isPhysical());
    }

    @Nullable
    private TResult resolve(@NotNull final Psi psi,
                            @NotNull final PsiResolver<Psi, TResult> resolver,
                            boolean needToPreventRecursion,
                            final boolean incompleteCode,
                            boolean isPhysical) {
        ProgressIndicatorProvider.checkCanceled();
        ApplicationManager.getApplication().assertReadAccessAllowed();

        ConcurrentMap<Psi, Getter<TResult>> map = getMap(isPhysical, incompleteCode);

        Getter<TResult> reference = map.get(psi);
        TResult result = reference == null ? null : reference.get();

        if (result != null) {
            return result;
        }

        RecursionGuard.StackStamp stamp = myGuard.markStack();
        result = needToPreventRecursion ? myGuard.doPreventingRecursion(Pair.create(psi, incompleteCode), true, new Computable<TResult>() {
            @Override
            public TResult compute() {
                return resolver.resolve(psi, incompleteCode);
            }
        }) : resolver.resolve(psi, incompleteCode);
        PsiElement element = result instanceof ResolveResult ? ((ResolveResult) result).getElement() : null;

        LOG.assertTrue(element == null || element.isValid(), result);

        if (stamp.mayCacheNow()) {
            cache(psi, map, result);
        }
        if(result != null && !resolver.checkResultClass(result))
            return null;
        return result;
    }

    private ConcurrentMap<Psi, Getter<TResult>> getMap(boolean physical, boolean incompleteCode) {
        //noinspection unchecked
        return myMaps[(physical ? 0 : 1) * 2 + (incompleteCode ? 0 : 1)];
    }

    private static class SoftGetter<T> extends SoftReference<T> implements Getter<T> {
        public SoftGetter(T referent) {
            super(referent);
        }
    }

    private static final Getter<Object> NULL_RESULT = new StaticGetter<>(null);

    private static <Psi extends PsiElement, TResult> void cache(@NotNull Psi psi, @NotNull ConcurrentMap<Psi, Getter<TResult>> map, TResult result) {
        // optimization: less contention
        Getter<TResult> cached = map.get(psi);
        if (cached != null && cached.get() == result) {
            return;
        }
        if (result == null) {
            // no use in creating SoftReference to null
            //noinspection unchecked
            cached = (Getter<TResult>) NULL_RESULT;
        } else {
            cached = new SoftGetter<>(result);
        }
        map.put(psi, cached);
    }
}
