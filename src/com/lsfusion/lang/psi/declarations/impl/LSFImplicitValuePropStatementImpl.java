package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.LSFStubBasedPsiElement;
import com.lsfusion.lang.psi.declarations.LSFImplicitValuePropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ImplicitValueStubElement;
import org.jetbrains.annotations.NotNull;

public abstract class LSFImplicitValuePropStatementImpl extends LSFStubBasedPsiElement<LSFImplicitValuePropStatement, ImplicitValueStubElement> implements LSFImplicitValuePropStatement {
    public LSFImplicitValuePropStatementImpl(@NotNull ImplicitValueStubElement stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LSFImplicitValuePropStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public LSFPropertyStatement getPropertyStatement() {
        return getImplicitInterfacePropertyStatement().getPropertyStatement();
    }
}
