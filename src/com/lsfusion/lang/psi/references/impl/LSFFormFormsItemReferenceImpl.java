package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFFormFormsDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFFormFormsItemReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;

public abstract class LSFFormFormsItemReferenceImpl extends LSFFormElementReferenceImpl<LSFFormFormsDeclaration> implements LSFFormFormsItemReference {
    protected LSFFormFormsItemReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected Function<LSFFormExtend, Collection<LSFFormFormsDeclaration>> getElementsCollector() {
        return LSFFormExtend::getFormDecls;
    }

    @Override
    protected boolean isDeclarationType(PsiElement element) {
        return element instanceof LSFFormFormsDeclaration;
    }
}
