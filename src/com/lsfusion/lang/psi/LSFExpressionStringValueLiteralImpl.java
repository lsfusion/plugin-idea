package com.lsfusion.lang.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class LSFExpressionStringValueLiteralImpl extends LSFLocalizedStringValueLiteralImpl implements LSFExpressionStringValueLiteral {
    public LSFExpressionStringValueLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull LSFVisitor visitor) {
        visitor.visitExpressionStringValueLiteral(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
        else super.accept(visitor);
    }
}
