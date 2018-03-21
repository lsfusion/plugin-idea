package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionStatement;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceActionStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfacePropStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public abstract class LSFExplicitInterfaceActionStatementImpl extends LSFExplicitInterfaceActionOrPropStatementImpl<ExplicitInterfaceActionStubElement> implements LSFExplicitInterfaceActionStatement {

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