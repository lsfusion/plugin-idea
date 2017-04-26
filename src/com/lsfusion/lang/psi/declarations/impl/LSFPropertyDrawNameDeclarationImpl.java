package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawNameDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LSFPropertyDrawNameDeclarationImpl extends LSFPropertyDrawDeclarationImpl implements LSFPropertyDrawNameDeclaration {

    protected LSFPropertyDrawNameDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
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

        LSFFormPropertyName formPropertyName = getFormPropertyName();
        if(formPropertyName == null)
            return null;
        return getNameIdentifier(formPropertyName);
    }

    @Override
    public LSFObjectUsageList getObjectUsageList() {
        return PsiTreeUtil.getParentOfType(this, LSFFormMappedNamePropertiesList.class).getObjectUsageList();
    }
}
