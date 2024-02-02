// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.LSFExpression;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.Inferred;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import java.util.Set;

public interface LSFPropertyExpression extends LSFExpression {

  @NotNull
  LSFIfPE getIfPE();

  @Nullable LSFExClassSet resolveValueClass(boolean infer);

  @NotNull List<LSFExprParamDeclaration> resolveParams();

  @NotNull Set<String> resolveAllParams();

  @Nullable LSFExClassSet resolveInferredValueClass(@Nullable InferExResult inferred);

  @NotNull Inferred inferParamClasses(@Nullable LSFExClassSet valueClass);

  List<String> getValueClassNames();

  List<String> getValuePropertyNames();

}
