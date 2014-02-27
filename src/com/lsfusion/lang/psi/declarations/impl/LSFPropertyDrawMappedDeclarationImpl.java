package com.lsfusion.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.psi.*;
import com.lsfusion.psi.declarations.LSFPropertyDrawMappedDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LSFPropertyDrawMappedDeclarationImpl extends LSFPropertyDrawDeclarationImpl implements LSFPropertyDrawMappedDeclaration {

    public LSFPropertyDrawMappedDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    protected abstract LSFFormPropertyObject getFormPropertyObject();

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        LSFSimpleName simpleName = getSimpleName();
        if(simpleName != null)
            return simpleName;
        
        return LSFPropertyDrawNameDeclarationImpl.getNameIdentifier(getFormPropertyObject().getFormPropertyName());
    }

    public LSFObjectUsageList getObjectUsageList() {
        return getFormPropertyObject().getObjectUsageList();
    }

    public LSFFormPropertyName getFormPropertyName() {
        return getFormPropertyObject().getFormPropertyName();
    }
}
