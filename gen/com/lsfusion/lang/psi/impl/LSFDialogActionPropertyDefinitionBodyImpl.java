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
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import java.util.Set;

public class LSFDialogActionPropertyDefinitionBodyImpl extends ASTWrapperPsiElement implements LSFDialogActionPropertyDefinitionBody {

  public LSFDialogActionPropertyDefinitionBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitDialogActionPropertyDefinitionBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<LSFContextFiltersClause> getContextFiltersClauseList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFContextFiltersClause.class);
  }

  @Override
  @Nullable
  public LSFCustomClassUsage getCustomClassUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFCustomClassUsage.class);
  }

  @Override
  @Nullable
  public LSFDoInputBody getDoInputBody() {
    return PsiTreeUtil.getChildOfType(this, LSFDoInputBody.class);
  }

  @Override
  @Nullable
  public LSFFormActionObjectList getFormActionObjectList() {
    return PsiTreeUtil.getChildOfType(this, LSFFormActionObjectList.class);
  }

  @Override
  @NotNull
  public List<LSFFormSessionScopeClause> getFormSessionScopeClauseList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormSessionScopeClause.class);
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
  public List<LSFManageSessionClause> getManageSessionClauseList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFManageSessionClause.class);
  }

  @Override
  @NotNull
  public List<LSFNoCancelClause> getNoCancelClauseList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFNoCancelClause.class);
  }

  @Override
  @NotNull
  public List<LSFWindowTypeLiteral> getWindowTypeLiteralList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFWindowTypeLiteral.class);
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
  public ContextModifier getDoContextModifier() {
    return LSFPsiImplUtil.getDoContextModifier(this);
  }

  @Override
  public ContextInferrer getDoContextInferrer() {
    return LSFPsiImplUtil.getDoContextInferrer(this);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
