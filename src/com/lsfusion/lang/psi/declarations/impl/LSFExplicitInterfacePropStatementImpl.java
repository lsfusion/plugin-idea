package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceStubElement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Set;

public abstract class LSFExplicitInterfacePropStatementImpl extends StubBasedPsiElementBase<ExplicitInterfaceStubElement> implements LSFExplicitInterfacePropStatement {
    public LSFExplicitInterfacePropStatementImpl(@NotNull ExplicitInterfaceStubElement stub, @NotNull IStubElementType nodeType) {
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
    public String getName() {
        ExplicitInterfaceStubElement stub = getStub();
        if(stub != null)
            return stub.getDeclName();
        return getPropertyStatement().getName();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return getPropertyStatement().getIcon(flags);
    }

    @Override
    public LSFExplicitClasses getExplicitParams() {
        ExplicitInterfaceStubElement stub = getStub();
        if(stub != null) {
            return stub.getParamExplicitClasses();
        }
        return ((LSFGlobalPropDeclarationImpl)getPropertyStatement()).getExplicitParams();
    }

    @Override
    public Set<String> getExplicitValues() {
        ExplicitInterfaceStubElement stub = getStub();
        if(stub != null) {
            return stub.getParamExplicitValues();
        }
        return ((LSFGlobalPropDeclarationImpl)getPropertyStatement()).getExplicitValues();
    }

    @Override
    public byte getPropType() {
        ExplicitInterfaceStubElement stub = getStub();
        if(stub != null) {
            return stub.getPropType();
        }
        return ((LSFGlobalPropDeclarationImpl)getPropertyStatement()).getPropType();
    }

    @Override
    public LSFFile getLSFFile() {
        return (LSFFile) getContainingFile();
    }

    @Override
    public List<LSFClassSet> resolveParamClasses() {
        LSFExplicitClasses explicitParams = getExplicitParams();
        if(explicitParams instanceof LSFExplicitSignature) {
            return LSFStringClassRef.resolve(((LSFExplicitSignature)explicitParams).signature, getLSFFile());
        }

        assert false; // по идее не должно заходить, только isLight
        return getPropertyStatement().resolveParamClasses();
    }

    @Override
    public String getParamPresentableText() {
        LSFExplicitClasses paramExplicitClasses = getExplicitParams();
        if(paramExplicitClasses instanceof LSFExplicitSignature)
            return LSFStringClassRef.getParamPresentableText(((LSFExplicitSignature)paramExplicitClasses).signature);

        assert false;
        return getPropertyStatement().getParamPresentableText();
    }

    @Override
    public String getValuePresentableText() {
        Set<String> paramExplicitValues = getExplicitValues();
        String valueText = "";
        if(paramExplicitValues != null) {
            valueText = StringUtils.join(paramExplicitValues,",");
        }
        return valueText + "?";
//        return ((LSFGlobalPropDeclarationImpl)getPropertyStatement()).getValuePresentableText();
    }

    @Override
    public PsiElement getLookupObject() {
        return this;
    }

    public Icon getIcon() {
        return LSFGlobalPropDeclarationImpl.getIcon(getPropType());
    }
}
