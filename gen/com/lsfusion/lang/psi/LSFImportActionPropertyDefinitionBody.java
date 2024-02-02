// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.ActionExpression;
import com.lsfusion.lang.psi.context.ExtendDoParamContext;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import java.util.Set;

public interface LSFImportActionPropertyDefinitionBody extends ActionExpression, ExtendDoParamContext {

  @Nullable
  LSFClassNameList getClassNameList();

  @Nullable
  LSFClassParamDeclareList getClassParamDeclareList();

  @Nullable
  LSFDoInputBody getDoInputBody();

  @Nullable
  LSFImportActionSourceType getImportActionSourceType();

  @Nullable
  LSFNonEmptyImportFieldDefinitions getNonEmptyImportFieldDefinitions();

  @Nullable
  LSFNonEmptyImportPropertyUsageListWithIds getNonEmptyImportPropertyUsageListWithIds();

  @Nullable
  LSFPropertyExpression getPropertyExpression();

  @Nullable
  LSFPropertyUsage getPropertyUsage();

  Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params);

  ContextModifier getDoContextModifier();

  ContextInferrer getDoContextInferrer();

  String getDocumentation(PsiElement child);

}
