package com.simpleplugin.psi.declarations;

import com.simpleplugin.classes.LSFClassSet;
import org.jetbrains.annotations.Nullable;

public interface LSFExprParamDeclaration extends LSFDeclaration {
    
    @Nullable
    LSFClassSet resolveClass();
}
