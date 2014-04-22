package com.lsfusion.lang.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class LSFStringValueLiteralImpl extends ASTWrapperPsiElement implements LSFStringValueLiteral {
    public LSFStringValueLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getValue() {
        String text = getText();
        return text.substring(1, text.lastIndexOf("'"));
    }
}                                     
