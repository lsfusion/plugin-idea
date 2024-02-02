// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.ActionExpression;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import java.util.Set;

public interface LSFCancelActionPropertyDefinitionBody extends ActionExpression {

  @Nullable
  LSFNonEmptyNoContextPropertyUsageList getNonEmptyNoContextPropertyUsageList();

  Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params);

  String getDocumentation(PsiElement child);

}
