// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.ExtendParamContext;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;

public interface LSFForActionPropertyMainBody extends ExtendParamContext {

  @Nullable
  LSFActionPropertyDefinitionBody getActionPropertyDefinitionBody();

  @Nullable
  LSFForAddObjClause getForAddObjClause();

  @Nullable
  LSFInlineOption getInlineOption();

  @Nullable
  LSFNonEmptyPropertyExpressionList getNonEmptyPropertyExpressionList();

  @Nullable
  LSFPropertyExpression getPropertyExpression();

  ContextModifier getContextModifier();

  ContextInferrer getContextInferrer();

  String getDocumentation(PsiElement child);

}
