package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFNamespaceUsage;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFExplicitNamespaceDeclaration;
import com.simpleplugin.psi.stubs.ExplicitNamespaceStubElement;
import org.jetbrains.annotations.NotNull;

public abstract class LSFExplicitNamespaceDeclarationImpl extends LSFNamespaceDeclarationImpl<LSFExplicitNamespaceDeclaration, ExplicitNamespaceStubElement> {

    public LSFExplicitNamespaceDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFExplicitNamespaceDeclarationImpl(@NotNull ExplicitNamespaceStubElement explicitNamespaceStubElement, @NotNull IStubElementType nodeType) {
        super(explicitNamespaceStubElement, nodeType);
    }

    protected abstract LSFNamespaceUsage getNamespaceUsage();

    @Override
    public LSFId getNameIdentifier() {
        return getNamespaceUsage().getSimpleName();
    }
}
