package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.declarations.LSFComponentDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LSFComponentDeclarationImpl extends LSFDeclarationImpl implements LSFComponentDeclaration {
    protected LSFComponentDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getMultiCompoundID();
    }
}
