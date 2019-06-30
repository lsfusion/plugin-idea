package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.openapi.util.Condition;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;

public interface LSFFormExtendElement extends LSFDeclaration {
    Condition<? extends LSFDeclaration> getDuplicateCondition();
    
    int getOffset();
}
