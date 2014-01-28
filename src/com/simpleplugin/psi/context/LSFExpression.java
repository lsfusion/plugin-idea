package com.simpleplugin.psi.context;

import com.intellij.psi.PsiElement;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.typeinfer.InferResult;
import com.simpleplugin.typeinfer.Inferred;
import org.jetbrains.annotations.Nullable;

public interface LSFExpression extends PsiElement {

    LSFClassSet resolveInferredValueClass(@Nullable InferResult inferred);

    Inferred inferParamClasses(LSFClassSet valueClass);
}
