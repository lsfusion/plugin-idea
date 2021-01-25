package com.lsfusion.lang.psi.declarations;

import com.intellij.openapi.util.Condition;
import com.lsfusion.util.BaseUtils;

public interface LSFDesignElementDeclaration<T extends LSFDesignElementDeclaration<T>> extends LSFDeclaration {
    default Condition<T> getDuplicateCondition() {
        return decl -> BaseUtils.nullEquals(getDeclName(), decl.getDeclName());
    }

    int getOffset();
}
