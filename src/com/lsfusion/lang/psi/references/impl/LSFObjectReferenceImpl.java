package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.declarations.LSFObjectDeclaration;
import com.lsfusion.lang.psi.references.LSFObjectReference;
import org.jetbrains.annotations.NotNull;

public abstract class LSFObjectReferenceImpl extends LSFAbstractParamReferenceImpl<LSFObjectDeclaration> implements LSFObjectReference {

    protected LSFObjectReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }
}
