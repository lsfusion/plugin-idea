package com.lsfusion.lang.psi.extend.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.Query;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.references.LSFFullNameReference;
import com.lsfusion.lang.psi.stubs.extend.ExtendStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendStubElementType;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class LSFExtendImpl<This extends LSFExtend<This, Stub>, Stub extends ExtendStubElement<This, Stub>> extends LSFStubBasedPsiElement<This, Stub> implements LSFExtend<This, Stub> {

    public LSFExtendImpl(@NotNull Stub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LSFExtendImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public LSFFullNameDeclaration resolveDecl() {
        LSFFullNameDeclaration extendingDeclaration = resolveExtendingDeclaration();
        if (extendingDeclaration != null) {
            return extendingDeclaration;
        }

        LSFFile lsfFile = getLSFFile();
        if (lsfFile == lsfFile.getOriginalFile()) {
            return getOwnDeclaration();
        }

        String namespace = lsfFile.getModuleDeclaration().getNamespace();

        //noinspection RedundantTypeArguments
        return LSFResolveUtil.<LSFFullNameDeclaration>singleResolve(
                LSFGlobalResolver.findElements(getGlobalName(), namespace, getStubTypes(), lsfFile, null, Conditions.<LSFFullNameDeclaration>alwaysTrue(), Finalizer.EMPTY)
        );
    }

    @Nullable
    protected abstract LSFFullNameDeclaration getOwnDeclaration();

    @Nullable
    protected LSFFullNameDeclaration resolveExtendingDeclaration() {
        LSFFullNameReference<LSFFullNameDeclaration, ?> extendingReference = getExtendingReference();
        if(extendingReference != null)
            return extendingReference.resolveDecl();
        return null;
    }

    @Nullable
    public abstract LSFFullNameReference getExtendingReference();

    protected Collection<FullNameStubElementType> getStubTypes() {
        return Collections.singleton(getStubType());
    }

    protected abstract FullNameStubElementType getStubType();

    protected static <T extends LSFDeclaration, This extends LSFExtend<This, Stub>, Stub extends ExtendStubElement<This, Stub>, Context>
                Set<T> processContext(PsiElement current, int offset, final Function<This, Collection<T>> processor,
                Function<PsiElement, Context> getContext, Function<Context, LSFFullNameDeclaration> resolveContextDecl, ExtendStubElementType<This, Stub> type) {
        Set<T> processedContext = processContext(current, processor, offset, false, getContext, resolveContextDecl, type);
        if (processedContext != null) {
            return processedContext;
        }

        PsiElement parent = current.getParent();
        if (!(parent == null || parent instanceof LSFFile)) {
            return processContext(parent, offset, processor, getContext, resolveContextDecl, type);
        }

        return new HashSet<>();
    }

    protected static <T extends LSFDeclaration, Extend extends LSFExtend<Extend, Stub>, Stub extends ExtendStubElement<Extend, Stub>, Context>
                Set<T> processContext(PsiElement current, final Function<Extend, Collection<T>> processor, final int offset, boolean ignoreUseBeforeDeclarationCheck,
                                      Function<PsiElement, Context> getContext, Function<Context, LSFFullNameDeclaration> resolveContextDecl, ExtendStubElementType<Extend, Stub> type) {
        Context context = getContext.apply(current);
        if (context != null)
            return processContext(resolveContextDecl.apply(context), (LSFFile) current.getContainingFile(), type, processor, offset, ignoreUseBeforeDeclarationCheck);
        return null;
    }

    // used for resolving + completion + duplicates
    private static <Extend extends LSFExtend<Extend, Stub>, Stub extends ExtendStubElement<Extend, Stub>> Query<Extend> findElements(LSFFullNameDeclaration decl, LSFFile file, ExtendStubElementType<Extend, Stub> type) {
        return LSFGlobalResolver.findExtendElements(decl, type, file);
    }

    // used for implementations
    protected static <Extend extends LSFExtend<Extend, Stub>, Stub extends ExtendStubElement<Extend, Stub>> List<PsiElement> processImplementationsSearch(LSFFullNameDeclaration decl, ExtendStubElementType<Extend, Stub> type) {
        List<PsiElement> names = new ArrayList<>();

        Project project = decl.getProject();
        for (Extend extend : LSFGlobalResolver.findExtendElements(decl, type, project, GlobalSearchScope.allScope(project))) {
            LSFFullNameReference extendingReference = extend.getExtendingReference();
            if (extendingReference != null)
                names.add(extendingReference);
        }

        return names;
    }
    protected static <T extends LSFDeclaration, Extend extends LSFExtend<Extend, Stub>, Stub extends ExtendStubElement<Extend, Stub>> Set<T> processContext(LSFFullNameDeclaration decl, LSFFile file, ExtendStubElementType<Extend, Stub> type, Function<Extend, Collection<T>> processor, Integer offset, boolean ignoreUseBeforeDeclarationCheck) {
        Set<T> finalResult = new HashSet<>();
        for(Extend formExtend : findElements(decl, file, type)) {
            boolean sameFile = offset != null && file == formExtend.getLSFFile();
            for(T element : processor.apply(formExtend))
                if(ignoreUseBeforeDeclarationCheck || !(sameFile && LSFGlobalResolver.isAfter(offset, element)))
                    finalResult.add(element);
        }
        return finalResult;
    }

    protected abstract ExtendStubElementType<This, Stub> getDuplicateExtendType();

    protected abstract List<Function<This, Collection<? extends LSFDeclaration>>> getDuplicateProcessors();

    protected <T extends LSFDeclaration> Set<LSFDeclaration> resolveDuplicates(Function<T, Condition<T>> duplicateCondition, Predicate<T> ignoreDuplicate) {
        Query<This> designs = findElements(resolveDecl(), getLSFFile(), getDuplicateExtendType());

        Set<LSFDeclaration> duplicates = new LinkedHashSet<>();
        for(Function<This, Collection<? extends LSFDeclaration>> duplicateProcessor : getDuplicateProcessors()) {
            Function<This, Collection<T>> typedProcessor = BaseUtils.immutableCast(duplicateProcessor);
            duplicates.addAll(resolveDuplicates(new ArrayList<>(typedProcessor.apply((This)this)), typedProcessor, designs, duplicateCondition, ignoreDuplicate));
        }
        return duplicates;
    }

    private <T extends LSFDeclaration> Set<T> resolveDuplicates(List<T> localDecls, final Function<This, Collection<T>> processor, Query<This> designs, Function<T, Condition<T>> duplicateCondition, Predicate<T> ignoreDuplicate) {
        Set<T> duplicates = new LinkedHashSet<>();

        for (int i = 0; i < localDecls.size(); i++) {
            T decl1 = localDecls.get(i);
            Condition<T> decl1Condition = duplicateCondition.apply(decl1);
            for (int j = i + 1; j < localDecls.size(); j++) {
                T decl2 = localDecls.get(j);
                if (decl1Condition.value(decl2)) {
                    checkAndAddDuplicate(duplicates, decl1, ignoreDuplicate);
                    checkAndAddDuplicate(duplicates, decl2, ignoreDuplicate);
                }
            }
        }

        final List<T> otherDecls = new ArrayList<>();
        if (designs != null) {
            designs.forEach(design -> {
                if (!LSFExtendImpl.this.equals(design)) {
                    otherDecls.addAll(processor.apply(design));
                }
                return true;
            });

            for (T decl1 : localDecls) {
                Condition<T> decl1Condition = duplicateCondition.apply(decl1);
                for (T decl2 : otherDecls) {
                    if (decl1Condition.value(decl2)) {
                        checkAndAddDuplicate(duplicates, decl1, ignoreDuplicate);
                    }
                }
            }
        }

        return duplicates;
    }

    private <T extends LSFDeclaration> void checkAndAddDuplicate(Set<T> duplicates, T decl, Predicate<T> ignoreDuplicate) {
        if (decl.getContainingFile().equals(getContainingFile()) && !ignoreDuplicate.test(decl)) {
            duplicates.add(decl);
        }
    }

}
