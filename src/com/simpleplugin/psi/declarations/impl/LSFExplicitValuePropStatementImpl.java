package com.simpleplugin.psi.declarations.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.LSFPropertyStatement;
import com.simpleplugin.psi.declarations.LSFExplicitValuePropStatement;
import com.simpleplugin.psi.stubs.interfaces.ExplicitValueStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LSFExplicitValuePropStatementImpl extends StubBasedPsiElementBase<ExplicitValueStubElement> implements LSFExplicitValuePropStatement {
    public LSFExplicitValuePropStatementImpl(@NotNull ExplicitValueStubElement stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LSFExplicitValuePropStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public LSFPropertyStatement getPropertyStatement() {
        return getImplicitValuePropertyStatement().getPropertyStatement();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return getPropertyStatement().getIcon(flags);
    }
}
