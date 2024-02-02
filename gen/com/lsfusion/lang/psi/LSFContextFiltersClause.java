// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.ExtendParamContext;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;

public interface LSFContextFiltersClause extends ExtendParamContext {

  @NotNull
  List<LSFPropertyExpression> getPropertyExpressionList();

  ContextModifier getContextModifier();

  ContextInferrer getContextInferrer();

}
