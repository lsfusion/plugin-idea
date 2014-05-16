package com.lsfusion.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.stubs.types.indexes.ModuleIndex;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.lsfusion.completion.CompletionUtils.getVariantsFromIndices;
import static com.lsfusion.util.LSFPsiUtils.subRange;

public abstract class FromJavaReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    protected final String referenceText;
    protected final String moduleName;
    protected final String namespaceName;
    protected final boolean searchInRequiredModules;

    public FromJavaReference(@NotNull PsiElement element, TextRange textRange, String moduleName) {
        this(element, textRange, moduleName, false);
    }
    
    public FromJavaReference(@NotNull PsiElement element, TextRange textRange, String moduleName, boolean searchInRequiredModules) {
        this(element, textRange, moduleName, null, searchInRequiredModules);
    }
    
    public FromJavaReference(@NotNull PsiElement element, TextRange textRange, String moduleName, String namespaceName, boolean searchInRequiredModules) {
        super(element, textRange);
        this.moduleName = moduleName;
        this.namespaceName = namespaceName;
        this.searchInRequiredModules = searchInRequiredModules;
        this.referenceText = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        PsiFile file = getElement().getContainingFile();
        final ResolveCache resolveCache = ResolveCache.getInstance(file.getProject());
        return resolveCache.resolveWithCaching(this, MyResolver.INSTANCE, false, false, file);
    }
    
    public ResolveResult[] doResolve() {
        GlobalSearchScope scope = getScope();
        if (scope == null) {
            return new ResolveResult[0];
        }

        List<ResolveResult> results = new ArrayList<ResolveResult>();
        for (LSFGlobalDeclaration decl : findDeclarations(scope)) {
            boolean add = true;
            if (namespaceName != null && decl instanceof LSFFullNameDeclaration) {
                if (!namespaceName.equals(((LSFFullNameDeclaration) decl).getNamespaceName())) {
                    add = false;
                }
            }
            
            if (add) {
                LSFId id = decl.getNameIdentifier();
                if (id != null) {
                    results.add(new PsiElementResolveResult(id));
                }
            }
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @Nullable
    public GlobalSearchScope getScope() {
        GlobalSearchScope projectScope = LSFPsiUtils.getModuleScope(myElement);

        if (moduleName != null) {
            Collection<LSFModuleDeclaration> modules = ModuleIndex.getInstance().get(moduleName, myElement.getProject(), projectScope);
            if (modules.isEmpty()) {
                return null;
            }

            GlobalSearchScope scope = GlobalSearchScope.EMPTY_SCOPE;

            List<VirtualFile> files = new ArrayList<VirtualFile>();

            for (LSFModuleDeclaration lsfModule : modules) {
                if (searchInRequiredModules) {
                    scope = scope.uniteWith(lsfModule.getLSFFile().getRequireScope());
                } else {
                    files.add(lsfModule.getLSFFile().getVirtualFile());
                }
            }

            return searchInRequiredModules
                   ? scope
                   : GlobalSearchScope.filesScope(myElement.getProject(), files);
        }
        
        return projectScope;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return getVariantsFromIndices(null, myElement.getProject(), Arrays.asList(getIndex()), 5, getScope()).toArray();
    }
    
    public TextRange getRangeInDocument() {
        return subRange(myElement.getTextRange(), getRangeInElement());
    }

    protected abstract Collection<? extends LSFGlobalDeclaration> findDeclarations(GlobalSearchScope scope);
    
    protected abstract StringStubIndexExtension getIndex();

    private static class MyResolver implements ResolveCache.PolyVariantResolver<FromJavaReference> {
        private static final MyResolver INSTANCE = new MyResolver();

        @NotNull
        @Override
        public ResolveResult[] resolve(@NotNull FromJavaReference reference, boolean incompleteCode) {
            return reference.doResolve();
        }
    }
}