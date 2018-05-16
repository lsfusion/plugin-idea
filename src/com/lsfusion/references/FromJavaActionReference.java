package com.lsfusion.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.lang.psi.declarations.LSFActionDeclaration;
import com.lsfusion.lang.psi.indexes.ActionIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class FromJavaActionReference extends FromJavaReference {

    public FromJavaActionReference(@NotNull PsiElement element, TextRange textRange, String moduleName, String namespaceName, boolean searchInRequiredModules) {
        super(element, textRange, moduleName, namespaceName, searchInRequiredModules);
    }

    @Override
    protected Collection<LSFActionDeclaration> findDeclarations(GlobalSearchScope scope) {
        return ActionIndex.getInstance().get(referenceText, myElement.getProject(), scope);
    }

    @Override
    protected ActionIndex getIndex() {
        return ActionIndex.getInstance();
    }
}