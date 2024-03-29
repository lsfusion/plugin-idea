package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionOrPropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceActionOrPropStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public abstract class LSFExplicitInterfaceActionOrPropStatementImpl<This extends LSFExplicitInterfaceActionOrPropStatement<This, Stub>, Stub extends ExplicitInterfaceActionOrPropStubElement<Stub, This>> extends LSFStubBasedPsiElement<This, Stub> implements LSFExplicitInterfaceActionOrPropStatement<This, Stub> {

    public LSFExplicitInterfaceActionOrPropStatementImpl(@NotNull Stub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LSFExplicitInterfaceActionOrPropStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFExplicitInterfaceActionOrPropStatementImpl(Stub stub, IElementType nodeType, ASTNode node) {
        super(stub, nodeType, node);
    }

    public abstract LSFActionOrGlobalPropDeclaration getDeclaration();
    @Override
    public String getName() {
        Stub stub = getStub();
        if(stub != null)
            return stub.getDeclName();
        return getDeclaration().getName();
    }

    @Override
    public LSFExplicitClasses getExplicitParams() {
        Stub stub = getStub();
        if(stub != null) {
            return stub.getParamExplicitClasses();
        }
        return getDeclaration().getExplicitParams();
    }

    @Override
    public byte getPropType() {
        Stub stub = getStub();
        if(stub != null) {
            return stub.getPropType();
        }
        return getDeclaration().getPropType();
    }

    @Override
    public List<LSFClassSet> resolveParamClasses() {
        LSFExplicitClasses explicitParams = getExplicitParams();
        if(explicitParams instanceof LSFExplicitSignature) {
            return LSFStringClassRef.resolve(((LSFExplicitSignature)explicitParams).signature, getLSFFile());
        }

        assert false; // по идее не должно заходить, только isLight
        return getDeclaration().resolveParamClasses();
    }

    @Override
    public boolean isNoParams() {
        LSFExplicitClasses explicitParams = getExplicitParams();
        if(explicitParams instanceof LSFExplicitSignature) {
            return ((LSFExplicitSignature) explicitParams).isNoParams();
        }

        assert false; // по идее не должно заходить, только isLight
        return resolveParamClasses().isEmpty();
    }

    @Override
    public String getParamPresentableText() {
        LSFExplicitClasses paramExplicitClasses = getExplicitParams();
        if(paramExplicitClasses instanceof LSFExplicitSignature)
            return LSFStringClassRef.getParamPresentableText(((LSFExplicitSignature)paramExplicitClasses).signature);

        assert false;
        return getDeclaration().getParamPresentableText();
    }

    @Override
    public LSFFile getLSFFile() {
        return (LSFFile) getContainingFile();
    }

    @Override
    public PsiElement getLookupObject() {
        return this;
    }

    public Icon getIcon() {
        return LSFActionOrGlobalPropDeclarationImpl.getIcon(getPropType());
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFActionOrGlobalPropDeclarationImpl.getIcon(getPropType());
    }
}
