package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFPropertyDrawNameDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LSFPropertyDrawNameDeclarationImpl extends LSFPropertyDrawDeclarationImpl implements LSFPropertyDrawNameDeclaration {

    protected LSFPropertyDrawNameDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public abstract LSFFormPropertyName getFormPropertyName();

    public static LSFId getNameIdentifier(LSFFormPropertyName name) {
        LSFPropertyUsage pUsage = name.getPropertyUsage();
        if(pUsage != null)
            return pUsage.getSimpleName();
        return name.getPredefinedFormPropertyName();
    }
    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        LSFSimpleName simpleName = getSimpleName();
        if(simpleName != null)
            return simpleName;

        return getNameIdentifier(getFormPropertyName());
    }

    @Override
    public LSFObjectUsageList getObjectUsageList() {
        return PsiTreeUtil.getParentOfType(this, LSFFormMappedNamePropertiesList.class).getObjectUsageList();
    }
}
