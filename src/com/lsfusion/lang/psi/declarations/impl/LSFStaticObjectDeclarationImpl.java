package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFSimpleNameWithCaption;
import com.lsfusion.lang.psi.declarations.LSFStaticObjectDeclaration;
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
