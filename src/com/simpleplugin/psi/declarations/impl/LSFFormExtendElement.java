package com.simpleplugin.psi.declarations.impl;

import com.intellij.openapi.util.Condition;
import com.simpleplugin.psi.declarations.LSFDeclaration;

public interface LSFFormExtendElement {
    Condition<? extends LSFDeclaration> getDuplicateCondition();
}
