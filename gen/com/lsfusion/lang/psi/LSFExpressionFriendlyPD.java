// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.LSFExpression;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.Inferred;
import com.lsfusion.lang.typeinfer.LSFExClassSet;

public interface LSFExpressionFriendlyPD extends LSFExpression {

  @Nullable
  LSFActiveTabPropertyDefinition getActiveTabPropertyDefinition();

  @Nullable
  LSFCasePropertyDefinition getCasePropertyDefinition();

  @Nullable
  LSFCastPropertyDefinition getCastPropertyDefinition();

  @Nullable
  LSFConcatPropertyDefinition getConcatPropertyDefinition();

  @Nullable
  LSFExpressionLiteral getExpressionLiteral();

  @Nullable
  LSFGroupExprPropertyDefinition getGroupExprPropertyDefinition();

  @Nullable
  LSFIfElsePropertyDefinition getIfElsePropertyDefinition();

  @Nullable
  LSFJoinPropertyDefinition getJoinPropertyDefinition();

  @Nullable
  LSFJsonFormPropertyDefinition getJsonFormPropertyDefinition();

  @Nullable
  LSFJsonPropertyDefinition getJsonPropertyDefinition();

  @Nullable
  LSFMaxPropertyDefinition getMaxPropertyDefinition();

  @Nullable
  LSFMultiPropertyDefinition getMultiPropertyDefinition();

  @Nullable
  LSFOverridePropertyDefinition getOverridePropertyDefinition();

  @Nullable
  LSFPartitionPropertyDefinition getPartitionPropertyDefinition();

  @Nullable
  LSFRecursivePropertyDefinition getRecursivePropertyDefinition();

  @Nullable
  LSFRoundPropertyDefinition getRoundPropertyDefinition();

  @Nullable
  LSFSessionPropertyDefinition getSessionPropertyDefinition();

  @Nullable
  LSFSignaturePropertyDefinition getSignaturePropertyDefinition();

  @Nullable
  LSFStructCreationPropertyDefinition getStructCreationPropertyDefinition();

  @Nullable LSFExClassSet resolveInferredValueClass(@Nullable InferExResult inferred);

  @NotNull Inferred inferParamClasses(@Nullable LSFExClassSet valueClass);

  List<String> getValueClassNames();

  List<String> getValuePropertyNames();

}
