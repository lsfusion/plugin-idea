package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFPropertyDeclaration;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFPropDeclaration;
import com.simpleplugin.psi.stubs.PropStubElement;
import org.jetbrains.annotations.NotNull;

public abstract class LSFPropDeclarationImpl extends LSFFullNameDeclarationImpl<LSFPropDeclaration, PropStubElement> implements LSFPropDeclaration {

    public LSFPropDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFPropDeclarationImpl(@NotNull PropStubElement propStubElement, @NotNull IStubElementType nodeType) {
        super(propStubElement, nodeType);
    }

    protected abstract LSFPropertyDeclaration getPropertyDeclaration();

    @Override
    public LSFId getNameIdentifier() {
        return getPropertyDeclaration().getSimpleNameWithCaption().getSimpleName();
    }
}
