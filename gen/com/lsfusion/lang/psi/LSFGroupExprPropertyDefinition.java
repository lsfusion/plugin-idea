// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.ExtendParamContext;
import com.lsfusion.lang.psi.context.LSFExpression;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.Inferred;
import com.lsfusion.lang.typeinfer.LSFExClassSet;

public interface LSFGroupExprPropertyDefinition extends ExtendParamContext, LSFExpression {

  @Nullable
  LSFGroupPropertyBody getGroupPropertyBody();

  @Nullable LSFExClassSet resolveInferredValueClass(InferExResult inferred);

  @NotNull Inferred inferParamClasses(@Nullable LSFExClassSet valueClass);

  ContextModifier getContextModifier();

  ContextInferrer getContextInferrer();

  List<String> getValueClassNames();

  List<String> getValuePropertyNames();

  String getDocumentation(PsiElement child);

}
