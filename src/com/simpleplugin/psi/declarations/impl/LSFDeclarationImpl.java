package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.Segment;
import com.intellij.psi.*;
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
        if (nameID == null) // есть declaration'ы с неявным ID и для них не всегда удобно подстраивать правила 
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

    @Override
    public boolean resolveDuplicates() {
        return false;
    }

    public static String getLocationString(LSFDeclaration decl) {
        final PsiFile file = decl.getLSFFile();
        final Document document = PsiDocumentManager.getInstance(decl.getProject()).getDocument(file);
        final SmartPsiElementPointer pointer = SmartPointerManager.getInstance(decl.getProject()).createSmartPsiElementPointer(decl);
        final Segment range = pointer.getRange();
        int lineNumber = -1;
        int linePosition = -1;
        if (document != null && range != null) {
            lineNumber = document.getLineNumber(range.getStartOffset()) + 1;
            linePosition = range.getStartOffset() - document.getLineStartOffset(lineNumber - 1) + 1;
        }

        return file.getName() + "(" + lineNumber + ":" + linePosition + ")";
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

    @Override
    public PsiElement[] processImplementationsSearch() {
        return processExtensionsSearch();
    }

    @Override
    public PsiElement[] processExtensionsSearch() {
        return PsiElement.EMPTY_ARRAY;
    }
}
