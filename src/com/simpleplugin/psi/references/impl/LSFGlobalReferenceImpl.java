package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.simpleplugin.psi.declarations.LSFDeclaration;
import com.simpleplugin.psi.references.LSFGlobalReference;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LSFGlobalReferenceImpl<T extends LSFDeclaration> extends LSFReferenceImpl<T> implements LSFGlobalReference<T> {

    protected LSFGlobalReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    public boolean isSoft() {
        return false;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return getPresentableText(this);
    }

    @Nullable
    @Override
    public String getLocationString() {
        return getLocationString(this);
    }

    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        return getIcon(this, unused);
    }

    @Override
    public ItemPresentation getPresentation() {
        return this;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return setName(this, name);
    }

    @Override
    public String getName() {
        return getName(this);
    }
}
