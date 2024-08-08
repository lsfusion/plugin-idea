package com.lsfusion.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.util.LSFStringUtils;
import org.jetbrains.annotations.NotNull;

import static com.lsfusion.lang.LSFElementGenerator.createLocalizedStringValueLiteral;
import static com.lsfusion.util.LSFStringUtils.*;

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
        String text = getText();
        if (isRawLiteral()) {
            return getRawLiteralValue(text);
        }
        return LSFStringUtils.getSimpleLiteralValue(text, "\\'{}$", true);
    }

    @Override
    public String getPropertiesFileValue() {
        assert !isRawLiteral();
        return LSFStringUtils.getSimpleLiteralPropertiesFileValue(getText(), "\\'{}$");
    }

    @Override
    public boolean needToBeLocalized() {
        if (isRawLiteral()) {
            return false;
        }
        return LSFStringUtils.hasLocalizationBlock(getText(), false);
    }

    @Override
    public boolean isVariable() {
        return needToBeLocalized();
    }
    
    @Override
    public boolean isRawLiteral() {
        return LSFStringUtils.isRawLiteral(getText());
    }
    
    @Override
    public PsiElement handleElementRename(@NotNull String newText) throws IncorrectOperationException {
        if (!isRawLiteral()) {
            LSFLocalizedStringValueLiteral newLiteral = createLocalizedStringValueLiteral(getProject(), newText);
            replace(newLiteral);
        }
        return this;
    }
}
