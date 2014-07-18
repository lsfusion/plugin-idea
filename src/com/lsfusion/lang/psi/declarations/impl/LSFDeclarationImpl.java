package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.LSFElementImpl;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class LSFDeclarationImpl extends LSFElementImpl implements LSFDeclaration {

    public LSFDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    // множественное наследование
    public static String getName(LSFDeclaration element) {
        LSFId nameID = element.getNameIdentifier();
        if (nameID == null) // есть declaration'ы с неявным ID и для них не всегда удобно подстраивать правила 
            return null;
        return nameID.getName();
    }

    public static PsiElement setName(LSFDeclaration element, @NonNls @NotNull String name, MetaTransaction transaction) throws IncorrectOperationException {
        element.getNameIdentifier().setName(name, transaction);
        return element;
    }

    public String getName() {
        return getName(this);
    }

    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return setName(this, name, null);
    }

    public void setName(@NonNls @NotNull String name, MetaTransaction transaction) {
        setName(this, name, transaction);
    }

    @Override
    public String getDeclName() {
        return getName();
    }

    @Override
    public boolean resolveDuplicates() {
        return false;
    }

    public static String getPresentableText(LSFDeclaration decl) {
        return decl.getDeclName();
    }

    @Override
    public Icon getIcon(boolean unused) {
        return getIcon(0);
    }

    @Override
    public Icon getIcon(int flags) {
        return null;
    }

    @Override
    public String getLocationString() {
        return LSFPsiUtils.getLocationString(this);
    }

    @Override
    public String getPresentableText() {
        return getPresentableText(this);
    }

    @Override
    public ItemPresentation getPresentation() {
        return this;
    }

    @Override
    public PsiElement[] processImplementationsSearch() {
        return PsiElement.EMPTY_ARRAY;
    }
}
