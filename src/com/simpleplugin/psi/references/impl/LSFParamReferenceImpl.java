package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.references.LSFParamReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LSFParamReferenceImpl extends LSFReferenceImpl implements LSFParamReference {

    public LSFParamReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isSoft() {
        return true;
    }

    @Override
    public LSFSimpleName getSimpleName() {
        throw new RuntimeException("not supported");
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return null;
    }
}
