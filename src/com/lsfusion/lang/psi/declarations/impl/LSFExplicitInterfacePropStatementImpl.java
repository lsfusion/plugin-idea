package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfacePropStubElement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Set;

public abstract class LSFExplicitInterfacePropStatementImpl extends LSFExplicitInterfaceActionOrPropStatementImpl<ExplicitInterfacePropStubElement> implements LSFExplicitInterfacePropStatement {
    public LSFExplicitInterfacePropStatementImpl(@NotNull ExplicitInterfacePropStubElement stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LSFExplicitInterfacePropStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public LSFPropertyStatement getPropertyStatement() {
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
        return ((LSFGlobalPropDeclarationImpl)getPropertyStatement()).getExplicitValues();
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
