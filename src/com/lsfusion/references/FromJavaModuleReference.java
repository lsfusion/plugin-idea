package com.lsfusion.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.stubs.types.indexes.ModuleIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class FromJavaModuleReference extends FromJavaReference {
    public FromJavaModuleReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange, null);
    }

    @Override
    protected Collection<LSFModuleDeclaration> findDeclarations(GlobalSearchScope scope) {
        return ModuleIndex.getInstance().get(referenceText, myElement.getProject(), scope);
    }

    @Override
    protected ModuleIndex getIndex() {
        return ModuleIndex.getInstance();
    }
}

