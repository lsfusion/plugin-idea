package com.simpleplugin.psi.declarations.impl;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.simpleplugin.psi.declarations.LSFPropertyDrawDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LSFPropertyDrawDeclarationImpl extends LSFFormElementDeclarationImpl implements LSFPropertyDrawDeclaration {

    protected LSFPropertyDrawDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return AllIcons.Nodes.PropertyRead;
    }
}
