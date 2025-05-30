package com.lsfusion.lang.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.properties.BundleNameEvaluator;
import com.intellij.lang.properties.PropertiesReferenceManager;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.lang.properties.references.PropertyReference;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.lang.LSFResourceBundleUtils;
import com.lsfusion.util.LSFStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class LSFReferencedStringValueLiteral extends ASTWrapperPsiElement implements LSFPropertiesFileValueGetter {
    public LSFReferencedStringValueLiteral(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        PsiReference[] refs = getReferences();
        return refs.length == 0 ? null : refs[0];
    }

    private List<PsiReference> findAllRefs(String literal) {
        List<LSFStringUtils.SpecialBlock> specialBlocks = getSpecialBlockList(literal);
        List<PsiReference> refs = new ArrayList<>();
        for (LSFStringUtils.SpecialBlock block : specialBlocks) {
            if (block.type == LSFStringUtils.StringSpecialBlockType.LOCALIZATION) {
                refs.add(new PropertyFileItemMultiReference(this, new TextRange(block.start, block.end+1)));
            }
        }
        return refs;
    }

    protected List<LSFStringUtils.SpecialBlock> getSpecialBlockList(String literal) {
        return LSFStringUtils.specialBlockList(literal, false);
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        return CachedValuesManager.getCachedValue(this, () -> {
            String literal = getText();
            List<PsiReference> refs = findAllRefs(literal);
            return CachedValueProvider.Result.create(refs.toArray(new PsiReference[0]),
                    PsiModificationTracker.MODIFICATION_COUNT);
        });
    }

    public class PropertyFileItemMultiReference extends PsiPolyVariantReferenceBase {
        public PropertyFileItemMultiReference(PsiElement element, TextRange range) {
            super(element, range);
        }

        private class AllScopePropertyReference extends PropertyReference {
            public AllScopePropertyReference(@NotNull String key, @NotNull PsiElement element, @Nullable String bundleName, boolean soft, TextRange range) {
                super(key, element, bundleName, soft, range);
            }

            @Override
            protected List<PropertiesFile> retrievePropertyFilesByBundleName(String resourceBundleName, PsiElement element) {
                // based on the com.intellij.lang.properties.references.I18nUtil.propertiesFilesByBundleName method
                if (resourceBundleName != null) {
                    PsiFile containingFile = element.getContainingFile();
                    PsiElement containingFileContext = InjectedLanguageManager.getInstance(containingFile.getProject()).getInjectionHost(containingFile);
                    if (containingFileContext != null) containingFile = containingFileContext.getContainingFile();

                    VirtualFile virtualFile = containingFile.getVirtualFile();
                    if (virtualFile == null) {
                        virtualFile = containingFile.getOriginalFile().getVirtualFile();
                    }

                    if (virtualFile != null) {
                        Project project = containingFile.getProject();
                        PropertiesReferenceManager refManager = PropertiesReferenceManager.getInstance(project);
                        Module module = ModuleUtil.findModuleForPsiElement(getElement());
                        if(module != null) // to cache result
                            return refManager.findPropertiesFiles(module, resourceBundleName);
                        return refManager.findPropertiesFiles(GlobalSearchScope.allScope(getProject()), resourceBundleName, BundleNameEvaluator.DEFAULT);
                    }
                }
                return Collections.emptyList();
            }
        }

        @NotNull
        @Override
        public ResolveResult[] multiResolve(boolean incompleteCode) {
            return ResolveCache.getInstance(getProject()).resolveWithCaching(PropertyFileItemMultiReference.this, PropertyFileItemMultiReference::resolveNoCache, true, incompleteCode);
        }

        private ResolveResult[] resolveNoCache(boolean incompleteCode) {
            Set<ResolveResult> results = new HashSet<>();
            String key = getReferenceId();

            Module module = ModuleUtil.findModuleForPsiElement(LSFReferencedStringValueLiteral.this);
            if (module != null) {
                for (VirtualFile file : LSFResourceBundleUtils.getScopeData(module, false).resourceBundleFiles) {
                    PropertyReference ref = new AllScopePropertyReference(key, LSFReferencedStringValueLiteral.this, file.getNameWithoutExtension(), true, getRangeInElement());
                    ResolveResult[] result = ref.multiResolve(incompleteCode);
                    Collections.addAll(results, result);
                }
            }
            return results.toArray(new ResolveResult[0]);
        }

        @NotNull
        @Override
        public String getCanonicalText() {
            return getReferenceId();
        }

        private String getReferenceId() {
            return LSFReferencedStringValueLiteral.this.getText().substring(
                    getRangeInElement().getStartOffset() + 1,
                    getRangeInElement().getEndOffset() - 1
            );
        }

        // do we need to override?
        @Override
        public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
            String newText = getText().substring(0, getRangeInElement().getStartOffset() + 1) + newElementName + getText().substring(getRangeInElement().getEndOffset() - 1);
            return LSFReferencedStringValueLiteral.this.handleElementRename(newText);
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            return new Object[0];
        }
    }

    abstract public PsiElement handleElementRename(@NotNull String newText);
}
