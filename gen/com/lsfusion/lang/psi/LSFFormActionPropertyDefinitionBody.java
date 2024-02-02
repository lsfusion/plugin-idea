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

public interface LSFFormActionPropertyDefinitionBody extends ActionExpression, FormContext {

  @NotNull
  List<LSFContextFiltersClause> getContextFiltersClauseList();

  @Nullable
  LSFCustomClassUsage getCustomClassUsage();

  @Nullable
  LSFEqualsSign getEqualsSign();

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

  @Nullable
  LSFStringLiteral getStringLiteral();

  @NotNull
  List<LSFSyncTypeLiteral> getSyncTypeLiteralList();

  @NotNull
  List<LSFWindowTypeLiteral> getWindowTypeLiteralList();

  Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params);

  @Nullable LSFFormDeclaration resolveFormDecl();

  String getDocumentation(PsiElement child);

}
