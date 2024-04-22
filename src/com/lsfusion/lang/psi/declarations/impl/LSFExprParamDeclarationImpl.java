package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class LSFExprParamDeclarationImpl extends LSFDeclarationImpl implements LSFExprParamDeclaration {

    protected LSFExprParamDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.PARAMETER;
    }
}
