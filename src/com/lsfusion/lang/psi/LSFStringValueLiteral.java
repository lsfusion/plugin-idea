package com.lsfusion.lang.psi;

import com.intellij.psi.PsiElement;
import com.lsfusion.util.LSFStringUtils;

public interface LSFStringValueLiteral extends PsiElement {
    default String getValue() {
        return LSFStringUtils.getSimpleLiteralValue(getText(), "\\'");
    }
}
