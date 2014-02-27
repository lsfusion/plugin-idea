package com.lsfusion.psi.declarations;

import com.lsfusion.classes.LSFClassSet;
import org.jetbrains.annotations.Nullable;

public interface LSFExprParamDeclaration extends LSFDeclaration {
    
    @Nullable
    LSFClassSet resolveClass();
}
