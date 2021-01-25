package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.declarations.LSFDesignElementDeclaration;
import org.jetbrains.annotations.NotNull;

public abstract class LSFDesignElementDeclarationImpl<T extends LSFDesignElementDeclaration<T>> extends LSFDeclarationImpl implements LSFDesignElementDeclaration<T> {

    public LSFDesignElementDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public int getOffset() {
        return getTextOffset();
    }
}
