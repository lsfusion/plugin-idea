package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.declarations.LSFFormFormsDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LSFFormFormsDeclarationImpl extends LSFFormElementDeclarationImpl<LSFFormFormsDeclaration> implements LSFFormFormsDeclaration {
    public LSFFormFormsDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getFormUsage().getCompoundID().getSimpleName();
    }
}
