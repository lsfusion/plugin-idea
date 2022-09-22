package com.lsfusion.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.lsfusion.util.LSFStringUtils;
import org.jetbrains.annotations.NotNull;

import static com.lsfusion.util.LSFStringUtils.unquote;

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

    @Override
    public boolean isVariable() {
        return LSFStringUtils.hasSpecialBlock(unquote(getText()), true);
    }

    @Override
    public boolean needToBeLocalized() {
        return LSFStringUtils.hasLocalizationBlock(unquote(getText()), true);
    }
}
