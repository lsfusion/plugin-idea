package com.lsfusion.lang.psi;

public interface LSFLocalizedStringValueLiteral extends LSFStringValueLiteral, LSFPropertiesFileValueGetter {
    boolean needToBeLocalized();
}
