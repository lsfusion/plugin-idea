package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public abstract class LSFPropertyDrawNameDeclarationImpl extends LSFPropertyDrawDeclarationImpl {

    protected LSFPropertyDrawNameDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
