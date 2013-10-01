package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public abstract class LSFPropertyDrawDeclarationImpl extends LSFFormElementDeclarationImpl {

    protected LSFPropertyDrawDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
