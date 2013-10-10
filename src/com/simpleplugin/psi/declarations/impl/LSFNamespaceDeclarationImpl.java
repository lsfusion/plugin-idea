package com.simpleplugin.psi.declarations.impl;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.declarations.LSFNamespaceDeclaration;
import com.simpleplugin.psi.stubs.NamespaceStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LSFNamespaceDeclarationImpl<This extends LSFNamespaceDeclaration<This, Stub>, Stub extends NamespaceStubElement<Stub, This>> extends LSFGlobalDeclarationImpl<This, Stub> implements LSFNamespaceDeclaration<This, Stub> {

    protected LSFNamespaceDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFNamespaceDeclarationImpl(@NotNull Stub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return AllIcons.Actions.Module;
    }

    @Override
    public String getPresentableText() {
        return getDeclName();
    }

    @Override
    public boolean resolveDuplicates() {
        return false;
    }
}
