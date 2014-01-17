package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.simpleplugin.LSFPsiImplUtil;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.classes.LSFValueClass;
import com.simpleplugin.meta.MetaTransaction;
import com.simpleplugin.psi.LSFClassName;
import com.simpleplugin.psi.LSFClassParamDeclare;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.context.ClassParamDeclareContext;
import com.simpleplugin.psi.declarations.LSFParamDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LSFParamDeclarationImpl extends LSFExprParamDeclarationImpl implements LSFParamDeclaration {

    protected LSFParamDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected abstract LSFSimpleName getSimpleName();

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getSimpleName();
    }

    @Nullable
    public LSFClassSet resolveClass() {
        return PsiTreeUtil.getParentOfType(this, ClassParamDeclareContext.class).resolveClass();
    }

    @Override
    public void ensureClass(LSFValueClass decl, MetaTransaction metaTrans) {
        PsiTreeUtil.getParentOfType(this, ClassParamDeclareContext.class).ensureClass(decl, metaTrans);
    }

    @Override
    public String getClassName() {
        PsiElement parent = getParent();
        if (parent instanceof LSFClassParamDeclare) {
            LSFClassName cName = ((LSFClassParamDeclare) parent).getClassName();
            if (cName != null) {
                return LSFPsiImplUtil.getClassName(cName);
            }
        }
        return null;
    }
}
