// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface LSFNavigatorElementOptions extends PsiElement {

  @NotNull
  List<LSFNavigatorElementInsertPosition> getNavigatorElementInsertPositionList();

  @NotNull
  List<LSFPropertyExpression> getPropertyExpressionList();

  @NotNull
  List<LSFStringLiteral> getStringLiteralList();

  @NotNull
  List<LSFWindowUsage> getWindowUsageList();

}
