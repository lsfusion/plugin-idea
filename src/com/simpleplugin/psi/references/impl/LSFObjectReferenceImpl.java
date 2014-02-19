package com.simpleplugin.psi.references.impl;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.ASTNode;
import com.simpleplugin.psi.declarations.LSFObjectDeclaration;
import com.simpleplugin.psi.references.LSFObjectReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class LSFObjectReferenceImpl extends LSFAbstractParamReferenceImpl<LSFObjectDeclaration> implements LSFObjectReference {

    protected LSFObjectReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }
}
