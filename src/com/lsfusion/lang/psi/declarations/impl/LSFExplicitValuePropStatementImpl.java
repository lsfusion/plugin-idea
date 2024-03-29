package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.LSFStubBasedPsiElement;
import com.lsfusion.lang.psi.declarations.LSFExplicitValuePropStatement;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFImplicitValuePropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitValueStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LSFExplicitValuePropStatementImpl extends LSFStubBasedPsiElement<LSFExplicitValuePropStatement, ExplicitValueStubElement> implements LSFExplicitValuePropStatement {
    public LSFExplicitValuePropStatementImpl(@NotNull ExplicitValueStubElement stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LSFExplicitValuePropStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    protected abstract LSFImplicitValuePropStatement getImplicitValuePropertyStatement();

    @NotNull
    @Override
    public LSFPropertyStatement getPropertyStatement() {
        return getImplicitValuePropertyStatement().getPropertyStatement();
    }

    @Override
    public LSFGlobalPropDeclaration getDeclaration() {
        return getPropertyStatement();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return getPropertyStatement().getIcon(flags);
    }
}
