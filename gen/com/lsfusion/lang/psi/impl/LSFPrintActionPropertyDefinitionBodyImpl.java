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
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import java.util.Set;

public class LSFPrintActionPropertyDefinitionBodyImpl extends ASTWrapperPsiElement implements LSFPrintActionPropertyDefinitionBody {

  public LSFPrintActionPropertyDefinitionBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitPrintActionPropertyDefinitionBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFContextFiltersClause getContextFiltersClause() {
    return PsiTreeUtil.getChildOfType(this, LSFContextFiltersClause.class);
  }

  @Override
  @Nullable
  public LSFCustomClassUsage getCustomClassUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFCustomClassUsage.class);
  }

  @Override
  @Nullable
  public LSFFormActionObjectList getFormActionObjectList() {
    return PsiTreeUtil.getChildOfType(this, LSFFormActionObjectList.class);
  }

  @Override
  @Nullable
  public LSFFormSingleActionObject getFormSingleActionObject() {
    return PsiTreeUtil.getChildOfType(this, LSFFormSingleActionObject.class);
  }

  @Override
  @Nullable
  public LSFFormUsage getFormUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFFormUsage.class);
  }

  @Override
  @NotNull
  public List<LSFPropertyExpression> getPropertyExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFPropertyExpression.class);
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
  @Nullable
  public LSFSyncTypeLiteral getSyncTypeLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFSyncTypeLiteral.class);
  }

  @Override
  public Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params) {
    return LSFPsiImplUtil.inferActionParamClasses(this, params);
  }

  @Override
  public @Nullable LSFFormDeclaration resolveFormDecl() {
    return LSFPsiImplUtil.resolveFormDecl(this);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
