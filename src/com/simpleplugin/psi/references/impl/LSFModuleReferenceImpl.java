package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.util.Query;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.declarations.LSFModuleDeclaration;
import com.simpleplugin.psi.references.LSFModuleReference;
import org.jetbrains.annotations.NotNull;

public abstract class LSFModuleReferenceImpl extends LSFGlobalReferenceImpl<LSFModuleDeclaration> implements LSFModuleReference {

    protected LSFModuleReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Query<LSFModuleDeclaration> resolveNoCache() {
        return LSFGlobalResolver.findModules(getNameRef(), getProject());
    }
}
