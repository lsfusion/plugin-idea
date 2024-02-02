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

public class LSFWindowOptionsImpl extends ASTWrapperPsiElement implements LSFWindowOptions {

  public LSFWindowOptionsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitWindowOptions(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<LSFAlignmentLiteral> getAlignmentLiteralList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFAlignmentLiteral.class);
  }

  @Override
  @NotNull
  public List<LSFBorderPosition> getBorderPositionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFBorderPosition.class);
  }

  @Override
  @NotNull
  public List<LSFDockPosition> getDockPositionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFDockPosition.class);
  }

  @Override
  @NotNull
  public List<LSFDrawRoot> getDrawRootList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFDrawRoot.class);
  }

  @Override
  @NotNull
  public List<LSFOrientation> getOrientationList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFOrientation.class);
  }

  @Override
  @NotNull
  public List<LSFPropertyExpression> getPropertyExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFPropertyExpression.class);
  }

}
