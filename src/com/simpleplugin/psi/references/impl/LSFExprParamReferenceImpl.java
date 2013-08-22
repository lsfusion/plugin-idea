package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFParamDeclare;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;
import org.jetbrains.annotations.NotNull;

public abstract class LSFExprParamReferenceImpl extends LSFAbstractParamReferenceImpl<LSFExprParamDeclaration> {
    
    protected LSFExprParamReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    protected abstract LSFParamDeclare getParamDeclare();

    @Override
    public LSFId getSimpleName() {
        return getParamDeclare().getSimpleName();
    }
}
