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

public class LSFListViewTypeImpl extends ASTWrapperPsiElement implements LSFListViewType {

  public LSFListViewTypeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitListViewType(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFCustomHeaderLiteral getCustomHeaderLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFCustomHeaderLiteral.class);
  }

  @Override
  @Nullable
  public LSFMapOptions getMapOptions() {
    return PsiTreeUtil.getChildOfType(this, LSFMapOptions.class);
  }

  @Override
  @Nullable
  public LSFPivotOptions getPivotOptions() {
    return PsiTreeUtil.getChildOfType(this, LSFPivotOptions.class);
  }

  @Override
  @Nullable
  public LSFPropertyExpression getPropertyExpression() {
    return PsiTreeUtil.getChildOfType(this, LSFPropertyExpression.class);
  }

  @Override
  @Nullable
  public LSFStringLiteral getStringLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFStringLiteral.class);
  }

}
