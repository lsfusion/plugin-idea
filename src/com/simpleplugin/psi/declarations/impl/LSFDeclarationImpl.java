package com.simpleplugin.psi.declarations.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.simpleplugin.LSFElementGenerator;
import com.simpleplugin.psi.LSFCompoundID;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFDeclaration;
import com.simpleplugin.psi.LSFTypes;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LSFDeclarationImpl extends ASTWrapperPsiElement implements LSFDeclaration {

    public LSFDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    // множественное наследование
    public static String getName(LSFDeclaration element) {
        return element.getNameIdentifier().getName();
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
}
