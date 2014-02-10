package com.simpleplugin.psi.declarations.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.LSFPropertyStatement;
import com.simpleplugin.psi.declarations.LSFImplicitValuePropStatement;
import com.simpleplugin.psi.stubs.interfaces.ImplicitValueStubElement;
import org.jetbrains.annotations.NotNull;

public abstract class LSFImplicitValuePropStatementImpl extends StubBasedPsiElementBase<ImplicitValueStubElement> implements LSFImplicitValuePropStatement {
    public LSFImplicitValuePropStatementImpl(@NotNull ImplicitValueStubElement stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LSFImplicitValuePropStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public LSFPropertyStatement getPropertyStatement() {
        return getImplicitInterfacePropertyStatement().getPropertyStatement();
    }
}
