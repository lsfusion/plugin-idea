package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public abstract class LSFExprParamDeclarationImpl extends LSFDeclarationImpl {

    protected LSFExprParamDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
