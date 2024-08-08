package com.lsfusion.lang.psi;

import com.intellij.psi.PsiElement;
import com.lsfusion.util.LSFStringUtils;

import static com.lsfusion.util.LSFStringUtils.*;

public interface LSFStringValueLiteral extends PsiElement {
    default String getValue() {
        String text = getText();
        if (LSFStringUtils.isRawLiteral(text)) {
            return getRawLiteralValue(text);
        }
        return getSimpleLiteralValue(text, "\\'", true);
    }
}
