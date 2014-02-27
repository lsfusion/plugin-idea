package com.lsfusion.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.psi.LSFClassParamDeclare;
import com.lsfusion.psi.LSFId;
import com.lsfusion.psi.declarations.LSFExprParamDeclaration;
import org.jetbrains.annotations.NotNull;

public abstract class LSFExprParamReferenceImpl extends LSFAbstractParamReferenceImpl<LSFExprParamDeclaration> {
    
    protected LSFExprParamReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    protected abstract LSFClassParamDeclare getClassParamDeclare();

    @Override
    protected PsiElement getParamDeclare() {
        return getClassParamDeclare().getParamDeclare();
    }

    @Override
    public LSFId getSimpleName() {
        return getClassParamDeclare().getParamDeclare().getSimpleName();
    }
}
