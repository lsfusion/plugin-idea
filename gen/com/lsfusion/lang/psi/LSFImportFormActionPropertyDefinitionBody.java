// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.ActionExpression;
import com.lsfusion.lang.psi.context.FormContext;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import java.util.Set;

public interface LSFImportFormActionPropertyDefinitionBody extends ActionExpression, FormContext {

  @NotNull
  LSFFormUsage getFormUsage();

  @NotNull
  List<LSFGroupObjectUsage> getGroupObjectUsageList();

  @Nullable
  LSFImportFormHierarchicalActionSourceType getImportFormHierarchicalActionSourceType();

  @Nullable
  LSFImportFormPlainActionSourceType getImportFormPlainActionSourceType();

  @NotNull
  List<LSFPropertyExpression> getPropertyExpressionList();

  Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params);

  @Nullable LSFFormDeclaration resolveFormDecl();

  String getDocumentation(PsiElement child);

}
