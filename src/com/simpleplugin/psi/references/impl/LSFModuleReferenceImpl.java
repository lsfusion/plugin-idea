package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.LSFDeclarationResolveResult;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.declarations.LSFModuleDeclaration;
import com.simpleplugin.psi.references.LSFModuleReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class LSFModuleReferenceImpl extends LSFGlobalReferenceImpl<LSFModuleDeclaration> implements LSFModuleReference {

    protected LSFModuleReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public LSFDeclarationResolveResult resolveNoCache() {
        Collection<LSFModuleDeclaration> decls = LSFGlobalResolver.findModules(getNameRef(), getScope()).findAll();
        return new LSFDeclarationResolveResult(decls, resolveDefaultErrorAnnotator(decls));
    }
}
