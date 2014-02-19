package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.LSFIcons;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleNameWithCaption;
import com.simpleplugin.psi.declarations.LSFStaticObjectDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LSFStaticObjectDeclarationImpl extends LSFDeclarationImpl implements LSFStaticObjectDeclaration {
    public LSFStaticObjectDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected abstract LSFSimpleNameWithCaption getSimpleNameWithCaption();

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getSimpleNameWithCaption().getSimpleName();
    }

    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.STATIC_OBJECT;
    }
}
