package com.simpleplugin.psi.references;

import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;
import com.simpleplugin.typeinfer.InferResult;
import com.simpleplugin.typeinfer.Inferred;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface LSFAbstractParamReference<T extends LSFExprParamDeclaration> extends LSFReference<T> {
    
    @Nullable
    LSFClassSet resolveClass();

    @Nullable
    LSFClassSet resolveInferredClass(@Nullable InferResult inferred);

}
