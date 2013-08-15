package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.util.EmptyQuery;
import com.intellij.util.Query;
import com.simpleplugin.psi.declarations.LSFParamDeclaration;
import com.simpleplugin.psi.references.LSFParamReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public abstract class LSFParamReferenceImpl extends LSFReferenceImpl<LSFParamDeclaration> implements LSFParamReference {

    public LSFParamReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isSoft() {
        return true;
    }

    @Override
    public Query<LSFParamDeclaration> resolveNoCache() {
        return new EmptyQuery<LSFParamDeclaration>();
    }

    @Override
    protected void fillListVariants(Collection<String> variants) {
    }
}
