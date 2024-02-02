// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.LSFExpression;
import com.lsfusion.lang.psi.context.PropertyUsageContext;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.Inferred;
import com.lsfusion.lang.typeinfer.LSFExClassSet;

public interface LSFPartitionPropertyDefinition extends LSFExpression, PropertyUsageContext {

  @Nullable
  LSFNonEmptyPropertyExpressionList getNonEmptyPropertyExpressionList();

  @Nullable
  LSFPartitionPropertyBy getPartitionPropertyBy();

  @Nullable
  LSFPropertyExpression getPropertyExpression();

  @Nullable
  LSFPropertyUsage getPropertyUsage();

  @Nullable LSFExClassSet resolveInferredValueClass(@Nullable InferExResult inferred);

  @Nullable List<LSFClassSet> resolveParamClasses();

  @NotNull Inferred inferParamClasses(@Nullable LSFExClassSet valueClass);

  @Nullable PsiElement getParamList();

  List<String> getValueClassNames();

  List<String> getValuePropertyNames();

  String getDocumentation(PsiElement child);

}
