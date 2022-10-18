package com.lsfusion.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.util.LSFStringUtils;
import org.jetbrains.annotations.NotNull;

import static com.lsfusion.lang.LSFElementGenerator.createLocalizedStringValueLiteral;

public class LSFLocalizedStringValueLiteralImpl extends LSFReferencedStringValueLiteral implements LSFLocalizedStringValueLiteral {
    public LSFLocalizedStringValueLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull LSFVisitor visitor) {
        visitor.visitLocalizedStringValueLiteral(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
        else super.accept(visitor);
    }

    @Override
    public String getValue() {
        return LSFStringUtils.getSimpleLiteralValue(getText(), "\\'{}");
    }

    @Override
    public String getPropertiesFileValue() {
        return LSFStringUtils.getSimpleLiteralPropertiesFileValue(getText(), "\\'{}");
    }

    @Override
    public boolean needToBeLocalized() {
        String text = getText();
        for (int i = 1; i + 1 < text.length(); ++i) {
            char ch = text.charAt(i);
            if (ch == '{' || ch == '}') {
                return true;
            } else if (ch == '\\') {
                ++i;
            }
        }
        return false;
    }

    // do we need to override?
    @Override
    public PsiElement handleElementRename(@NotNull String newText) throws IncorrectOperationException {
        LSFLocalizedStringValueLiteral newLiteral = createLocalizedStringValueLiteral(getProject(), newText);
        replace(newLiteral);
        return null;
    }
}
