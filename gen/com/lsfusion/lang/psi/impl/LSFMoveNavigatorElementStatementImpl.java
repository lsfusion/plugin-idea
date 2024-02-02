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

public class LSFMoveNavigatorElementStatementImpl extends ASTWrapperPsiElement implements LSFMoveNavigatorElementStatement {

  public LSFMoveNavigatorElementStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitMoveNavigatorElementStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFLocalizedStringLiteral getLocalizedStringLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFLocalizedStringLiteral.class);
  }

  @Override
  @Nullable
  public LSFNavigatorElementOptions getNavigatorElementOptions() {
    return PsiTreeUtil.getChildOfType(this, LSFNavigatorElementOptions.class);
  }

  @Override
  @Nullable
  public LSFNavigatorElementSelector getNavigatorElementSelector() {
    return PsiTreeUtil.getChildOfType(this, LSFNavigatorElementSelector.class);
  }

  @Override
  @Nullable
  public LSFNavigatorElementStatementBody getNavigatorElementStatementBody() {
    return PsiTreeUtil.getChildOfType(this, LSFNavigatorElementStatementBody.class);
  }

}
