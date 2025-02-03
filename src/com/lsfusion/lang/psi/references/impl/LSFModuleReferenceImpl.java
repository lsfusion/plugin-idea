package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.LSFResolveResult;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.references.LSFModuleReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class LSFModuleReferenceImpl extends LSFGlobalReferenceImpl<LSFModuleDeclaration> implements LSFModuleReference {

    protected LSFModuleReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public LSFResolveResult resolveNoCache() {
        Collection<LSFModuleDeclaration> decls = LSFGlobalResolver.findModules(getNameRef(), getLSFFile().getScope()).findAll();
        return new LSFResolveResult(decls, resolveDefaultErrorAnnotator(decls, false));
    }

    @Override
    protected boolean isDeclarationType(PsiElement element) {
        return element instanceof LSFModuleDeclaration;
    }
}
