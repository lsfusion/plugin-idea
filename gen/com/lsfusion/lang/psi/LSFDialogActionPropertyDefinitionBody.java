// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.ActionExpression;
import com.lsfusion.lang.psi.context.FormContext;
import com.lsfusion.lang.psi.context.ExtendDoParamContext;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import java.util.Set;

public interface LSFDialogActionPropertyDefinitionBody extends ActionExpression, FormContext, ExtendDoParamContext {

  @NotNull
  List<LSFContextFiltersClause> getContextFiltersClauseList();

  @Nullable
  LSFCustomClassUsage getCustomClassUsage();

  @Nullable
  LSFDoInputBody getDoInputBody();

  @Nullable
  LSFFormActionObjectList getFormActionObjectList();

  @NotNull
  List<LSFFormSessionScopeClause> getFormSessionScopeClauseList();

  @Nullable
  LSFFormSingleActionObject getFormSingleActionObject();

  @Nullable
  LSFFormUsage getFormUsage();

  @NotNull
  List<LSFManageSessionClause> getManageSessionClauseList();

  @NotNull
  List<LSFNoCancelClause> getNoCancelClauseList();

  @NotNull
  List<LSFWindowTypeLiteral> getWindowTypeLiteralList();

  Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params);

  @Nullable LSFFormDeclaration resolveFormDecl();

  ContextModifier getDoContextModifier();

  ContextInferrer getDoContextInferrer();

  String getDocumentation(PsiElement child);

}
