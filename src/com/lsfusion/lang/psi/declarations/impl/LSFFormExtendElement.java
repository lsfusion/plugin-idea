package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.openapi.util.Condition;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.util.BaseUtils;

public interface LSFFormExtendElement<T extends LSFFormExtendElement<T>> extends LSFDeclaration {

    default Condition<T> getDuplicateCondition() {
        return decl -> BaseUtils.nullEquals(getDeclName(), decl.getDeclName());
    }
    
    int getOffset();
}
