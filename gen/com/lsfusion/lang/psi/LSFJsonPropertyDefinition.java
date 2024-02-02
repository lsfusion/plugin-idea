// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.LSFExpression;
import com.lsfusion.lang.psi.context.ExtendParamContext;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.Inferred;
import com.lsfusion.lang.typeinfer.LSFExClassSet;

public interface LSFJsonPropertyDefinition extends LSFExpression, ExtendParamContext {

  @NotNull
  LSFNonEmptyAliasedPropertyExpressionList getNonEmptyAliasedPropertyExpressionList();

  @NotNull
  List<LSFPropertyExpressionWithOrder> getPropertyExpressionWithOrderList();

  @Nullable
  LSFWherePropertyExpression getWherePropertyExpression();

  @Nullable LSFExClassSet resolveInferredValueClass(@Nullable InferExResult inferred);

  @NotNull Inferred inferParamClasses(@Nullable LSFExClassSet valueClass);

  List<String> getValueClassNames();

  List<String> getValuePropertyNames();

  ContextModifier getContextModifier();

  ContextInferrer getContextInferrer();

  String getDocumentation(PsiElement child);

}
