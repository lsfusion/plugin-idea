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

public class LSFGroupPropertyBodyImpl extends ASTWrapperPsiElement implements LSFGroupPropertyBody {

  public LSFGroupPropertyBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitGroupPropertyBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFGroupingType getGroupingType() {
    return PsiTreeUtil.getChildOfType(this, LSFGroupingType.class);
  }

  @Override
  @Nullable
  public LSFGroupingTypeOrder getGroupingTypeOrder() {
    return PsiTreeUtil.getChildOfType(this, LSFGroupingTypeOrder.class);
  }

  @Override
  @NotNull
  public LSFNonEmptyPropertyExpressionList getNonEmptyPropertyExpressionList() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFNonEmptyPropertyExpressionList.class));
  }

  @Override
  @Nullable
  public LSFOrderPropertyBy getOrderPropertyBy() {
    return PsiTreeUtil.getChildOfType(this, LSFOrderPropertyBy.class);
  }

  @Override
  @Nullable
  public LSFPropertyExpression getPropertyExpression() {
    return PsiTreeUtil.getChildOfType(this, LSFPropertyExpression.class);
  }

}
