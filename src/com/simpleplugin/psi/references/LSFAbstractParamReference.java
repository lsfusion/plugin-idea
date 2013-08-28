package com.simpleplugin.psi.references;

import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;
import org.jetbrains.annotations.Nullable;

public interface LSFAbstractParamReference<T extends LSFExprParamDeclaration> extends LSFReference<T> {
    
    @Nullable
    LSFClassSet resolveClass();
}
