package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFPropertyDrawMappedDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LSFPropertyDrawMappedDeclarationImpl extends LSFPropertyDrawDeclarationImpl implements LSFPropertyDrawMappedDeclaration {

    public LSFPropertyDrawMappedDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    protected abstract LSFFormMappedProperty getFormMappedProperty();

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        LSFSimpleName simpleName = getSimpleName();
        if(simpleName != null)
            return simpleName;
        
        return LSFPropertyDrawNameDeclarationImpl.getNameIdentifier(getFormMappedProperty().getFormPropertyName());
    }

    public LSFObjectUsageList getObjectUsageList() {
        return getFormMappedProperty().getObjectUsageList();
    }

    public LSFFormPropertyName getFormPropertyName() {
        return getFormMappedProperty().getFormPropertyName();
    }
}
