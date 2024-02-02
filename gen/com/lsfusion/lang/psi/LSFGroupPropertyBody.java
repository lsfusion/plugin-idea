// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface LSFGroupPropertyBody extends PsiElement {

  @Nullable
  LSFGroupingType getGroupingType();

  @Nullable
  LSFGroupingTypeOrder getGroupingTypeOrder();

  @NotNull
  LSFNonEmptyPropertyExpressionList getNonEmptyPropertyExpressionList();

  @Nullable
  LSFOrderPropertyBy getOrderPropertyBy();

  @Nullable
  LSFPropertyExpression getPropertyExpression();

}
