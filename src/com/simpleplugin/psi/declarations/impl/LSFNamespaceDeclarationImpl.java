package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.declarations.LSFNamespaceDeclaration;
import com.simpleplugin.psi.stubs.NamespaceStubElement;
import org.jetbrains.annotations.NotNull;

public abstract class LSFNamespaceDeclarationImpl<This extends LSFNamespaceDeclaration<This, Stub>, Stub extends NamespaceStubElement<Stub, This>> extends LSFGlobalDeclarationImpl<This, Stub> implements LSFNamespaceDeclaration<This, Stub> {

    protected LSFNamespaceDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFNamespaceDeclarationImpl(@NotNull Stub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }
}
