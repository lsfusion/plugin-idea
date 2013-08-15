package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFLocalPropDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LSFLocalPropDeclarationImpl extends LSFDeclarationImpl implements LSFLocalPropDeclaration {

    public LSFLocalPropDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public abstract LSFSimpleName getSimpleName();

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getSimpleName();
    }
}
