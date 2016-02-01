package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.LSFStubBasedPsiElement;
import com.lsfusion.lang.psi.declarations.LSFGlobalDeclaration;
import com.lsfusion.lang.psi.stubs.GlobalStubElement;
import com.lsfusion.refactoring.ElementMigration;
import com.lsfusion.util.LSFPsiUtils;
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
        if (stub != null)
            return stub.getGlobalName();

        return getName();
    }

    @Override
    public String getName() {
        Stub stub = getStub();
        if (stub != null) // оптимизация
            return stub.getGlobalName();

        return LSFDeclarationImpl.getName(this);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return LSFDeclarationImpl.setName(this, name, null);
    }

    public void setName(@NonNls @NotNull String name, MetaTransaction transaction) {
        LSFDeclarationImpl.setName(this, name, transaction);
    }

    @Override
    public Icon getIcon(boolean unused) {
        return getIcon(0);
    }

    @Override
    public Icon getIcon(int flags) {
        return null;
    }

    @Override
    public String getLocationString() {
        return LSFPsiUtils.getLocationString(this);
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

    @Override
    public PsiElement[] processImplementationsSearch() {
        return PsiElement.EMPTY_ARRAY;
    }

    @Override
    public ElementMigration getMigration(String newName) {
        return LSFDeclarationImpl.getMigration(this, newName);
    }

    public PsiElement getLookupObject() { // пока не совсем понятно зачем
        return this;
    }
}
