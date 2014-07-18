package com.lsfusion.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.indexes.PropIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class FromJavaPropertyReference extends FromJavaReference {
    public FromJavaPropertyReference(@NotNull PsiElement element, TextRange textRange, String moduleName) {
        super(element, textRange, moduleName);
    }

    public FromJavaPropertyReference(@NotNull PsiElement element, TextRange textRange, String moduleName, boolean searchInRequiredModules) {
        super(element, textRange, moduleName, searchInRequiredModules);
    }

    public FromJavaPropertyReference(@NotNull PsiElement element, TextRange textRange, String moduleName, String namespaceName, boolean searchInRequiredModules) {
        super(element, textRange, moduleName, namespaceName, searchInRequiredModules);
    }

    @Override
    protected Collection<LSFGlobalPropDeclaration> findDeclarations(GlobalSearchScope scope) {
        return PropIndex.getInstance().get(referenceText, myElement.getProject(), scope);
    }

    @Override
    protected PropIndex getIndex() {
        return PropIndex.getInstance();
    }
}