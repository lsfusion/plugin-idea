package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.LSFResolveResult;
import com.lsfusion.lang.psi.declarations.LSFNamespaceDeclaration;
import com.lsfusion.lang.psi.references.LSFNamespaceReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class LSFNamespaceReferenceImpl extends LSFGlobalReferenceImpl<LSFNamespaceDeclaration> implements LSFNamespaceReference {

    public LSFNamespaceReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public LSFResolveResult resolveNoCache() {
        Collection<LSFNamespaceDeclaration> decls = LSFGlobalResolver.findNamespaces(getNameRef(), getScope()).findAll();
        return new LSFResolveResult(decls, resolveDefaultErrorAnnotator(decls));
    }
}
