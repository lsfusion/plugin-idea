package com.lsfusion.lang.psi.references;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.Nullable;

public interface LSFExprParamReference extends LSFReference<LSFExprParamDeclaration> {

    @Nullable
    LSFClassSet resolveClass();

    @Nullable
    LSFExClassSet resolveInferredClass(@Nullable InferExResult inferred);
}
