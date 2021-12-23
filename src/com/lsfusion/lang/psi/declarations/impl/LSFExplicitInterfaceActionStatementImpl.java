package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.lang.psi.LSFActionStatement;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceActionStubElement;
import org.jetbrains.annotations.NotNull;

public abstract class LSFExplicitInterfaceActionStatementImpl extends LSFExplicitInterfaceActionOrPropStatementImpl<LSFExplicitInterfaceActionStatement, ExplicitInterfaceActionStubElement> implements LSFExplicitInterfaceActionStatement {

    public LSFExplicitInterfaceActionStatementImpl(@NotNull ExplicitInterfaceActionStubElement stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }
    
    public LSFExplicitInterfaceActionStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public abstract LSFActionStatement getActionStatement();

    @Override
    public LSFActionOrGlobalPropDeclaration getDeclaration() {
        return getActionStatement();
    }

    @Override
    public boolean isAction() {
        return true;
    }

    @Override
    public String getValuePresentableText() {
        return "";
    }
}