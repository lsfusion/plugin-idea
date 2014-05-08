package com.lsfusion.lang.psi.references.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.lang.psi.LSFStringLiteral;
import com.lsfusion.lang.psi.references.LSFJavaClassStringReference;
import org.jetbrains.annotations.NotNull;

import static com.lsfusion.lang.LSFElementGenerator.createStringLiteralFromText;

public abstract class LSFJavaClassStringReferenceImpl extends ASTWrapperPsiElement implements LSFJavaClassStringReference {

    public LSFJavaClassStringReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        final Module module = ModuleUtilCore.findModuleForPsiElement(this);
        JavaClassReferenceProvider provider;
        if (module != null) {
            provider = new JavaClassReferenceProvider() {
                @Override
                public GlobalSearchScope getScope(Project project) {
                    return GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
                }
            };
        } else {
            provider = new JavaClassReferenceProvider();
        }
        provider.setSoft(false);
        provider.setOption(JavaClassReferenceProvider.CONCRETE, Boolean.TRUE);
        provider.setOption(JavaClassReferenceProvider.NOT_INTERFACE, Boolean.TRUE);
        provider.setOption(JavaClassReferenceProvider.NOT_ENUM, Boolean.TRUE);

        return provider.getReferencesByElement(this);
    }

    @Override
    public void setNewText(String newText) {
        LSFStringLiteral newLiteral = createStringLiteralFromText(getProject(), newText);
        getStringLiteral().replace(newLiteral);
    }

    @NotNull
    @Override
    public abstract LSFStringLiteral getStringLiteral();
}
