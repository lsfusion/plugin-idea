package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.LSFDeclarationResolveResult;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.declarations.LSFNamespaceDeclaration;
import com.simpleplugin.psi.references.LSFNamespaceReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class LSFNamespaceReferenceImpl extends LSFGlobalReferenceImpl<LSFNamespaceDeclaration> implements LSFNamespaceReference {

    public LSFNamespaceReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public LSFDeclarationResolveResult resolveNoCache() {
        Collection<LSFNamespaceDeclaration> decls = LSFGlobalResolver.findNamespaces(getNameRef(), getScope()).findAll();
        return new LSFDeclarationResolveResult(decls, resolveDefaultErrorAnnotator(decls));
    }
}
