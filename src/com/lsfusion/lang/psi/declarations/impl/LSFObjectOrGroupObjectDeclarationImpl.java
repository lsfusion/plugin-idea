package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.declarations.LSFObjectOrGroupObjectDeclaration;
import org.jetbrains.annotations.NotNull;

public abstract class LSFObjectOrGroupObjectDeclarationImpl<T extends LSFObjectOrGroupObjectDeclaration<T>> extends LSFFormElementDeclarationImpl<T> implements LSFObjectOrGroupObjectDeclaration<T> {

    protected LSFObjectOrGroupObjectDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
