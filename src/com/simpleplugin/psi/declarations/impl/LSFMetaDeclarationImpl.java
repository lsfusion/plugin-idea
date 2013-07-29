package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFMetaDeclaration;
import com.simpleplugin.psi.stubs.MetaStubElement;
import org.jetbrains.annotations.NotNull;

public abstract class LSFMetaDeclarationImpl extends LSFFullNameDeclarationImpl<LSFMetaDeclaration, MetaStubElement> implements LSFMetaDeclaration {

    public LSFMetaDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFMetaDeclarationImpl(@NotNull MetaStubElement metaStubElement, @NotNull IStubElementType nodeType) {
        super(metaStubElement, nodeType);
    }

    protected abstract LSFSimpleName getSimpleName();

    public abstract LSFMetaDeclIdList getMetaDeclIdList();

    @Override
    public int getParamCount() {
        MetaStubElement stub = getStub();
        if(stub != null)
            return stub.getParamCount();

        return getMetaDeclIdList().getMetaDeclIdList().size();
    }

    @Override
    public LSFId getNameIdentifier() {
        return getSimpleName();
    }
}
