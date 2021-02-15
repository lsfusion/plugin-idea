package com.lsfusion.lang.psi.declarations;

import com.intellij.openapi.util.Condition;
import com.lsfusion.util.BaseUtils;

public interface LSFStaticObjectDeclaration extends LSFDeclaration {

    default Condition<LSFStaticObjectDeclaration> getDuplicateCondition() {
        return decl -> BaseUtils.nullEquals(getDeclName(), decl.getDeclName());
    }
}
