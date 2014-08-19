package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.InferResult;
import com.lsfusion.lang.typeinfer.Inferred;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LSFExpression extends PsiElement {

    LSFExClassSet resolveInferredValueClass(@Nullable InferExResult inferred);

    Inferred inferParamClasses(@Nullable LSFExClassSet valueClass);

    List<String> getValueClassNames();

    List<String> getValuePropertyNames();
}
