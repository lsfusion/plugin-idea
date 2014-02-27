package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.typeinfer.InferResult;
import com.lsfusion.lang.typeinfer.Inferred;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LSFExpression extends PsiElement {

    LSFClassSet resolveInferredValueClass(@Nullable InferResult inferred);

    Inferred inferParamClasses(@Nullable LSFClassSet valueClass);

    List<String> getValueClassNames();

    List<String> getValuePropertyNames();
}
