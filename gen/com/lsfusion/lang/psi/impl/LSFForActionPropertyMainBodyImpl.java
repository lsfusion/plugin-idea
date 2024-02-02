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
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;

public class LSFForActionPropertyMainBodyImpl extends ASTWrapperPsiElement implements LSFForActionPropertyMainBody {

  public LSFForActionPropertyMainBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitForActionPropertyMainBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFActionPropertyDefinitionBody getActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFForAddObjClause getForAddObjClause() {
    return PsiTreeUtil.getChildOfType(this, LSFForAddObjClause.class);
  }

  @Override
  @Nullable
  public LSFInlineOption getInlineOption() {
    return PsiTreeUtil.getChildOfType(this, LSFInlineOption.class);
  }

  @Override
  @Nullable
  public LSFNonEmptyPropertyExpressionList getNonEmptyPropertyExpressionList() {
    return PsiTreeUtil.getChildOfType(this, LSFNonEmptyPropertyExpressionList.class);
  }

  @Override
  @Nullable
  public LSFPropertyExpression getPropertyExpression() {
    return PsiTreeUtil.getChildOfType(this, LSFPropertyExpression.class);
  }

  @Override
  public ContextModifier getContextModifier() {
    return LSFPsiImplUtil.getContextModifier(this);
  }

  @Override
  public ContextInferrer getContextInferrer() {
    return LSFPsiImplUtil.getContextInferrer(this);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
