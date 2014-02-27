package com.lsfusion.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.psi.LSFPsiImplUtil;
import com.lsfusion.classes.LSFClassSet;
import com.lsfusion.classes.LSFValueClass;
import com.lsfusion.meta.MetaTransaction;
import com.lsfusion.psi.LSFClassName;
import com.lsfusion.psi.LSFClassParamDeclare;
import com.lsfusion.psi.LSFId;
import com.lsfusion.psi.LSFSimpleName;
import com.lsfusion.psi.context.ClassParamDeclareContext;
import com.lsfusion.psi.declarations.LSFParamDeclaration;
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
