package com.simpleplugin.psi.declarations.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.declarations.LSFExplicitInterfacePropStatement;
import com.simpleplugin.psi.stubs.ExplicitInterfaceStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LSFExplicitInterfacePropStatementImpl extends StubBasedPsiElementBase<ExplicitInterfaceStubElement> implements LSFExplicitInterfacePropStatement {
    public LSFExplicitInterfacePropStatementImpl(@NotNull ExplicitInterfaceStubElement stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LSFExplicitInterfacePropStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return getPropertyStatement().getIcon(flags);
    }
}
