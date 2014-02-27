package com.lsfusion.lang.psi.references;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.InferResult;
import org.jetbrains.annotations.Nullable;

public interface LSFAbstractParamReference<T extends LSFExprParamDeclaration> extends LSFReference<T> {
    
    @Nullable
    LSFClassSet resolveClass();

    @Nullable
    LSFClassSet resolveInferredClass(@Nullable InferResult inferred);

}
