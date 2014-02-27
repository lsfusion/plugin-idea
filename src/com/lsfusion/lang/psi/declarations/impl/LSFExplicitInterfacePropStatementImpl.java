package com.lsfusion.psi.declarations.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.psi.LSFPropertyStatement;
import com.lsfusion.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.psi.stubs.interfaces.ExplicitInterfaceStubElement;
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

    @Override
    public LSFPropertyStatement getPropertyStatement() {
        return getExplicitValuePropertyStatement().getPropertyStatement();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return getPropertyStatement().getIcon(flags);
    }
}
