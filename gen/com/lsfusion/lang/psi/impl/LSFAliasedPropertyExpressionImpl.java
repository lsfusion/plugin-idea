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

public class LSFAliasedPropertyExpressionImpl extends ASTWrapperPsiElement implements LSFAliasedPropertyExpression {

  public LSFAliasedPropertyExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitAliasedPropertyExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFPropertyExpression getPropertyExpression() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFPropertyExpression.class));
  }

  @Override
  @Nullable
  public LSFStringLiteral getStringLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFStringLiteral.class);
  }

}
