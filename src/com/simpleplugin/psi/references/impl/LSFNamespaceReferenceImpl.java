package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.util.Query;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.declarations.LSFNamespaceDeclaration;
import com.simpleplugin.psi.references.LSFNamespaceReference;
import org.jetbrains.annotations.NotNull;

public abstract class LSFNamespaceReferenceImpl extends LSFGlobalReferenceImpl<LSFNamespaceDeclaration> implements LSFNamespaceReference {

    public LSFNamespaceReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Query<LSFNamespaceDeclaration> resolveNoCache() {
        return LSFGlobalResolver.findNamespaces(getNameRef(), getScope());
    }
}
