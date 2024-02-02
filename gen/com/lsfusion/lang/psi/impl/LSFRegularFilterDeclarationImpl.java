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

public class LSFRegularFilterDeclarationImpl extends ASTWrapperPsiElement implements LSFRegularFilterDeclaration {

  public LSFRegularFilterDeclarationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitRegularFilterDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFFilterSetDefault getFilterSetDefault() {
    return PsiTreeUtil.getChildOfType(this, LSFFilterSetDefault.class);
  }

  @Override
  @NotNull
  public LSFFormExprDeclaration getFormExprDeclaration() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFFormExprDeclaration.class));
  }

  @Override
  @NotNull
  public LSFLocalizedStringLiteral getLocalizedStringLiteral() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFLocalizedStringLiteral.class));
  }

  @Override
  @Nullable
  public LSFStringLiteral getStringLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFStringLiteral.class);
  }

}
