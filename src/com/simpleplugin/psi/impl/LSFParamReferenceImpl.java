package com.simpleplugin.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.simpleplugin.psi.LSFParamReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFParamReferenceImpl extends LSFReferenceImpl implements LSFParamReference {

    public LSFParamReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isSoft() {
        return true;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return null;
    }
}
