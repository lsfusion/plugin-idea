package com.lsfusion.lang.psi;

public interface LSFExpressionStringValueLiteral extends LSFLocalizedStringValueLiteral {
    String getValue();

    boolean needToBeLocalized();
}
