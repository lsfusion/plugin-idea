package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.LSFSimpleNameWithCaption;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.stubs.ClassStubElement;
import org.jetbrains.annotations.NotNull;

public abstract class LSFClassDeclarationImpl extends LSFFullNameDeclarationImpl<LSFClassDeclaration, ClassStubElement> implements LSFClassDeclaration {

    public LSFClassDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFClassDeclarationImpl(@NotNull ClassStubElement classStubElement, @NotNull IStubElementType nodeType) {
        super(classStubElement, nodeType);
    }

    protected abstract LSFSimpleNameWithCaption getSimpleNameWithCaption();

    @Override
    public LSFId getNameIdentifier() {
        return getSimpleNameWithCaption().getSimpleName();
    }
}
