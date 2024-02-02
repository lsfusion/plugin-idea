// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.ActionExpression;
import com.lsfusion.lang.psi.context.ClassParamDeclareContext;
import com.lsfusion.lang.psi.context.ExtendDoParamContext;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import java.util.Set;

public interface LSFInputActionPropertyDefinitionBody extends ActionExpression, ClassParamDeclareContext, ExtendDoParamContext {

  @Nullable
  LSFBuiltInClassName getBuiltInClassName();

  @Nullable
  LSFChangeInputPropertyCustomView getChangeInputPropertyCustomView();

  @Nullable
  LSFClassOrExpression getClassOrExpression();

  @Nullable
  LSFContextActions getContextActions();

  @Nullable
  LSFDoInputBody getDoInputBody();

  @Nullable
  LSFFormSessionScopeClause getFormSessionScopeClause();

  @Nullable
  LSFListWhereInputProps getListWhereInputProps();

  @Nullable
  LSFParamDeclare getParamDeclare();

  @Nullable
  LSFPropertyExpression getPropertyExpression();

  @Nullable
  LSFStaticDestination getStaticDestination();

  Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params);

  @Nullable LSFClassSet resolveClass();

  void ensureClass(@NotNull LSFValueClass valueClass, MetaTransaction metaTrans);

  ContextModifier getDoContextModifier();

  ContextInferrer getDoContextInferrer();

  String getDocumentation(PsiElement child);

}
