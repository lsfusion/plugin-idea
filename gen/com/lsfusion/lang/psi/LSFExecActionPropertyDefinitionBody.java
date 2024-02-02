// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.PropertyUsageContext;
import com.lsfusion.lang.psi.context.ActionExpression;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import java.util.Set;

public interface LSFExecActionPropertyDefinitionBody extends PropertyUsageContext, ActionExpression {

  @Nullable
  LSFActionUsage getActionUsage();

  @Nullable
  LSFPropertyExpressionList getPropertyExpressionList();

  @Nullable List<LSFClassSet> resolveParamClasses();

  Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params);

  @Nullable PsiElement getParamList();

  String getDocumentation(PsiElement child);

}
