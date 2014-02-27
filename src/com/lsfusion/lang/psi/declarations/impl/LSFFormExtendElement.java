package com.lsfusion.psi.declarations.impl;

import com.intellij.openapi.util.Condition;
import com.lsfusion.psi.declarations.LSFDeclaration;

public interface LSFFormExtendElement {
    Condition<? extends LSFDeclaration> getDuplicateCondition();
}
