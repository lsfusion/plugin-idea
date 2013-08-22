package com.simpleplugin.psi.declarations.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.simpleplugin.psi.LSFElementImpl;
import com.simpleplugin.psi.LSFFile;
import com.simpleplugin.psi.LSFStubBasedPsiElement;
import com.simpleplugin.psi.declarations.LSFGlobalDeclaration;
import com.simpleplugin.psi.stubs.GlobalStubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

// множественное наследова
public abstract class LSFGlobalDeclarationImpl<This extends LSFGlobalDeclaration<This, Stub>, Stub extends GlobalStubElement<Stub, This>> extends LSFStubBasedPsiElement<This, Stub> implements LSFGlobalDeclaration<This, Stub> {

    protected LSFGlobalDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFGlobalDeclarationImpl(@NotNull Stub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public String getDeclName() {
        Stub stub = getStub();
        if(stub!=null)
            return stub.getGlobalName();

        return getName();
    }

    @Override
    public String getName() {
        return LSFDeclarationImpl.getName(this);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return LSFDeclarationImpl.setName(this, name);
    }

    @Override
    public Icon getIcon(boolean unused) {
        return LSFDeclarationImpl.getIcon(this, unused);
    }

    @Override
    public String getLocationString() {
        return LSFDeclarationImpl.getLocationString(this);
    }

    @Override
    public String getPresentableText() {
        return LSFDeclarationImpl.getPresentableText(this);
    }

    @Override
    public ItemPresentation getPresentation() {
        return this;
    }

    @Override
    public String getGlobalName() {
        return getDeclName();
    }
}
