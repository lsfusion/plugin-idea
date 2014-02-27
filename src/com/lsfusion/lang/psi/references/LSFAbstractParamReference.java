package com.lsfusion.psi.references;

import com.lsfusion.classes.LSFClassSet;
import com.lsfusion.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.typeinfer.InferResult;
import org.jetbrains.annotations.Nullable;

public interface LSFAbstractParamReference<T extends LSFExprParamDeclaration> extends LSFReference<T> {
    
    @Nullable
    LSFClassSet resolveClass();

    @Nullable
    LSFClassSet resolveInferredClass(@Nullable InferResult inferred);

}
