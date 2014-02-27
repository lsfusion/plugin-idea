package com.lsfusion.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.psi.LSFResolveResult;
import com.lsfusion.psi.LSFGlobalResolver;
import com.lsfusion.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.psi.references.LSFModuleReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class LSFModuleReferenceImpl extends LSFGlobalReferenceImpl<LSFModuleDeclaration> implements LSFModuleReference {

    protected LSFModuleReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public LSFResolveResult resolveNoCache() {
        Collection<LSFModuleDeclaration> decls = LSFGlobalResolver.findModules(getNameRef(), getScope()).findAll();
        return new LSFResolveResult(decls, resolveDefaultErrorAnnotator(decls));
    }
}
