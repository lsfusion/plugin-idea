package com.simpleplugin.psi.declarations.impl;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleNameWithCaption;
import com.simpleplugin.psi.declarations.LSFFormDeclaration;
import com.simpleplugin.psi.stubs.FormStubElement;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LSFFormDeclarationImpl extends LSFFullNameDeclarationImpl<LSFFormDeclaration, FormStubElement> implements LSFFormDeclaration {

    public LSFFormDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFFormDeclarationImpl(@NotNull FormStubElement formStubElement, @NotNull IStubElementType nodeType) {
        super(formStubElement, nodeType);
    }

    protected abstract LSFSimpleNameWithCaption getSimpleNameWithCaption();

    @Override
    public LSFId getNameIdentifier() {
        return getSimpleNameWithCaption().getSimpleName();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return AllIcons.FileTypes.UiForm;
    }

    @Override
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.FORM;
    }
}
