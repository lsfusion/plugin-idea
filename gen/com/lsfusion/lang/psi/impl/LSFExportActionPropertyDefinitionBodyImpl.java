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

public class LSFExportActionPropertyDefinitionBodyImpl extends ASTWrapperPsiElement implements LSFExportActionPropertyDefinitionBody {

  public LSFExportActionPropertyDefinitionBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitExportActionPropertyDefinitionBody(this);
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
  @Nullable
  public LSFGroupObjectDestination getGroupObjectDestination() {
    return PsiTreeUtil.getChildOfType(this, LSFGroupObjectDestination.class);
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
  public LSFSelectTops getSelectTops() {
    return PsiTreeUtil.getChildOfType(this, LSFSelectTops.class);
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
