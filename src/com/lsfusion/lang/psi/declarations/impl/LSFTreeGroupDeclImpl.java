package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.declarations.LSFTreeGroupDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LSFTreeGroupDeclImpl extends LSFFormElementDeclarationImpl<LSFTreeGroupDecl> implements LSFTreeGroupDecl {
    public LSFTreeGroupDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getSimpleName();
    }
}
