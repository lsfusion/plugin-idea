package com.lsfusion.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.stubs.types.indexes.ClassIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class FromJavaClassReference extends FromJavaReference {
    public FromJavaClassReference(@NotNull PsiElement element, TextRange textRange, String moduleName) {
        super(element, textRange, moduleName);
    }
    
    public FromJavaClassReference(@NotNull PsiElement element, TextRange textRange, String moduleName, boolean searchInRequiredModules) {
        super(element, textRange, moduleName, searchInRequiredModules);
    }

    public FromJavaClassReference(@NotNull PsiElement element, TextRange textRange, String moduleName, String namespaceName, boolean searchInRequiredModules) {
        super(element, textRange, moduleName, namespaceName, searchInRequiredModules);
    }

    @Override
    protected Collection<LSFClassDeclaration> findDeclarations(GlobalSearchScope scope) {
        return ClassIndex.getInstance().get(referenceText, myElement.getProject(), scope);
    }

    @Override
    protected ClassIndex getIndex() {
        return ClassIndex.getInstance();
    }
}