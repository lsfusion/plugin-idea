// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.lsfusion.lang.psi.*;

public class LSFNavigatorElementOptionsImpl extends ASTWrapperPsiElement implements LSFNavigatorElementOptions {

  public LSFNavigatorElementOptionsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitNavigatorElementOptions(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<LSFNavigatorElementInsertPosition> getNavigatorElementInsertPositionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFNavigatorElementInsertPosition.class);
  }

  @Override
  @NotNull
  public List<LSFPropertyExpression> getPropertyExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFPropertyExpression.class);
  }

  @Override
  @NotNull
  public List<LSFStringLiteral> getStringLiteralList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFStringLiteral.class);
  }

  @Override
  @NotNull
  public List<LSFWindowUsage> getWindowUsageList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFWindowUsage.class);
  }

}
