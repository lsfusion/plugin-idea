package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.LSFIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class LSFExprParamDeclarationImpl extends LSFDeclarationImpl {

    protected LSFExprParamDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.PARAMETER;
    }
}
