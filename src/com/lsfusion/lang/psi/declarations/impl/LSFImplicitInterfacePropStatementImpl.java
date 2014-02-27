package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.lang.psi.declarations.LSFImplicitInterfacePropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ImplicitInterfaceStubElement;
import org.jetbrains.annotations.NotNull;

public abstract class LSFImplicitInterfacePropStatementImpl extends StubBasedPsiElementBase<ImplicitInterfaceStubElement> implements LSFImplicitInterfacePropStatement {
    public LSFImplicitInterfacePropStatementImpl(@NotNull ImplicitInterfaceStubElement stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LSFImplicitInterfacePropStatementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
