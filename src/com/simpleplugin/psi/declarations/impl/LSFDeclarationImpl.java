package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.simpleplugin.psi.LSFElementImpl;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.declarations.LSFDeclaration;
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
        if(nameID == null) // есть declaration'ы с неявным ID и для них не всегда удобно подстраивать правила 
            return null;
        return nameID.getName();
    }

    public static PsiElement setName(LSFDeclaration element, @NonNls @NotNull String name) throws IncorrectOperationException {
        element.getNameIdentifier().setName(name);
        return element;
    }

    public String getName() {
        return getName(this);
    }

    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return setName(this, name);
    }

    @Override
    public String getDeclName() {
        return getName();
    }

    public static Icon getIcon(LSFDeclaration decl, boolean unused) {
        return decl.getIcon(0);
    }

    public static String getLocationString(LSFDeclaration decl) {
        return decl.getLSFFile().getModuleDeclaration().getDeclName();
    }

    public static String getPresentableText(LSFDeclaration decl) {
        return decl.getPresentableText();
    }

    @Override
    public Icon getIcon(boolean unused) {
        return getIcon(this, unused);
    }

    @Override
    public String getLocationString() {
        return getLocationString(this);
    }

    @Override
    public String getPresentableText() {
        return getPresentableText(this);
    }

    @Override
    public ItemPresentation getPresentation() {
        return this;
    }
}
