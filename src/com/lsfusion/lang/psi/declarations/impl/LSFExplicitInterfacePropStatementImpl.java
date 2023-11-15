package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.declarations.LSFExplicitValuePropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfacePropStubElement;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Set;

public abstract class LSFExplicitInterfacePropStatementImpl extends LSFExplicitInterfaceActionOrPropStatementImpl<LSFExplicitInterfacePropStatement, ExplicitInterfacePropStubElement> implements LSFExplicitInterfacePropStatement {
    public LSFExplicitInterfacePropStatementImpl(@NotNull ExplicitInterfacePropStubElement stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LSFExplicitInterfacePropStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected abstract LSFExplicitValuePropStatement getExplicitValuePropertyStatement(); 

    @NotNull
    protected LSFPropertyStatement getPropertyStatement() {
        return getExplicitValuePropertyStatement().getPropertyStatement();
    }

    @Override
    public LSFActionOrGlobalPropDeclaration getDeclaration() {
        return getPropertyStatement();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return getPropertyStatement().getIcon(flags);
    }

    @Override
    public Set<String> getExplicitValues() {
        ExplicitInterfacePropStubElement stub = getStub();
        if(stub != null) {
            return stub.getParamExplicitValues();
        }
        return ((LSFStatementGlobalPropDeclarationImpl)getPropertyStatement()).getExplicitValues();
    }

    @Override
    public String getValuePresentableText() {
        Set<String> paramExplicitValues = getExplicitValues();
        String valueText = "";
        if(paramExplicitValues != null) {
            valueText = StringUtils.join(paramExplicitValues,",");
        }
        return ": " + valueText + "?";
//        return ((LSFGlobalPropDeclarationImpl)getPropertyStatement()).getValuePresentableText();
    }

    @Override
    public boolean isAction() {
        return false;
    }
}
