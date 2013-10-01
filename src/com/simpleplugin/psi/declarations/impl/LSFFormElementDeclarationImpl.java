package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.psi.declarations.LSFFormElementDeclaration;
import org.jetbrains.annotations.NotNull;

public abstract class LSFFormElementDeclarationImpl extends LSFDeclarationImpl implements LSFFormElementDeclaration {

    protected LSFFormElementDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
