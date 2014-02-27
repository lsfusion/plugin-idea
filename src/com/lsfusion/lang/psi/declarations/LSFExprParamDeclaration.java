package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.classes.LSFClassSet;
import org.jetbrains.annotations.Nullable;

public interface LSFExprParamDeclaration extends LSFDeclaration {
    
    @Nullable
    LSFClassSet resolveClass();
}
