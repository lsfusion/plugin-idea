package com.lsfusion.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.util.LSFStringUtils;
import org.jetbrains.annotations.NotNull;

import static com.lsfusion.lang.LSFElementGenerator.createMetacodeStringValueLiteral;

public class LSFMetacodeStringValueLiteralImpl extends LSFReferencedStringValueLiteral implements LSFMetacodeStringValueLiteral  {
    public LSFMetacodeStringValueLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull LSFVisitor visitor) {
        visitor.visitMetacodeStringValueLiteral(this);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
        else super.accept(visitor);
    }

    @Override
    public String getPropertiesFileValue() {
        return LSFStringUtils.getSimpleLiteralPropertiesFileValue(getText(), "\\'");
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newText) throws IncorrectOperationException {
        LSFMetacodeStringValueLiteral newLiteral = createMetacodeStringValueLiteral(getProject(), newText);
        replace(newLiteral);
        return this;
    }
}
