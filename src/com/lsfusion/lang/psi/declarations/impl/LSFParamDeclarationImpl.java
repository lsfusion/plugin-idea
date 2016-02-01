package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.ClassParamDeclareContext;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;
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
    public LSFStringClassRef getClassName() {
        PsiElement parent = getParent();
        if (parent instanceof LSFClassParamDeclare) {
            LSFClassName cName = ((LSFClassParamDeclare) parent).getClassName();
            if (cName != null) {
                return LSFPsiImplUtil.getClassNameRef(cName);
            }
        }
        return null;
    }
}
