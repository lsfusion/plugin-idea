package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.LSFIcons;
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
