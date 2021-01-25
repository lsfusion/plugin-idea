package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFormElementDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class LSFFormElementDeclarationImpl<T extends LSFFormElementDeclaration<T>> extends LSFDeclarationImpl implements LSFFormElementDeclaration<T> {

    public interface Processor<T> {
        Collection<T> process(LSFFormExtend formExtend);
    }

    protected LSFFormElementDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public int getOffset() {
        return getTextOffset();
    }

    @NotNull
    @Override
    public LSFFormExtend getFormExtend() {
        return PsiTreeUtil.getParentOfType(this, LSFFormExtend.class);
    }

    @Nullable
    @Override
    public LSFFormDeclaration resolveFormDecl() {
        return (LSFFormDeclaration) getFormExtend().resolveDecl();
    }
}
