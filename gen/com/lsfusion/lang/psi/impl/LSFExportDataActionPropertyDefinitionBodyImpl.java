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
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import java.util.Set;

public class LSFExportDataActionPropertyDefinitionBodyImpl extends ASTWrapperPsiElement implements LSFExportDataActionPropertyDefinitionBody {

  public LSFExportDataActionPropertyDefinitionBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitExportDataActionPropertyDefinitionBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFHasHeaderOption getHasHeaderOption() {
    return PsiTreeUtil.getChildOfType(this, LSFHasHeaderOption.class);
  }

  @Override
  @Nullable
  public LSFNoEscapeOption getNoEscapeOption() {
    return PsiTreeUtil.getChildOfType(this, LSFNoEscapeOption.class);
  }

  @Override
  @Nullable
  public LSFNonEmptyAliasedPropertyExpressionList getNonEmptyAliasedPropertyExpressionList() {
    return PsiTreeUtil.getChildOfType(this, LSFNonEmptyAliasedPropertyExpressionList.class);
  }

  @Override
  @NotNull
  public List<LSFPropertyExpression> getPropertyExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFPropertyExpression.class);
  }

  @Override
  @NotNull
  public List<LSFPropertyExpressionWithOrder> getPropertyExpressionWithOrderList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFPropertyExpressionWithOrder.class);
  }

  @Override
  @Nullable
  public LSFSelectTop getSelectTop() {
    return PsiTreeUtil.getChildOfType(this, LSFSelectTop.class);
  }

  @Override
  @Nullable
  public LSFSheetExpression getSheetExpression() {
    return PsiTreeUtil.getChildOfType(this, LSFSheetExpression.class);
  }

  @Override
  @Nullable
  public LSFStaticDestination getStaticDestination() {
    return PsiTreeUtil.getChildOfType(this, LSFStaticDestination.class);
  }

  @Override
  @NotNull
  public List<LSFStringLiteral> getStringLiteralList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFStringLiteral.class);
  }

  @Override
  @Nullable
  public LSFWherePropertyExpression getWherePropertyExpression() {
    return PsiTreeUtil.getChildOfType(this, LSFWherePropertyExpression.class);
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
  public Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params) {
    return LSFPsiImplUtil.inferActionParamClasses(this, params);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
