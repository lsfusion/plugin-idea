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

public class LSFImportActionPropertyDefinitionBodyImpl extends ASTWrapperPsiElement implements LSFImportActionPropertyDefinitionBody {

  public LSFImportActionPropertyDefinitionBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitImportActionPropertyDefinitionBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFClassNameList getClassNameList() {
    return PsiTreeUtil.getChildOfType(this, LSFClassNameList.class);
  }

  @Override
  @Nullable
  public LSFClassParamDeclareList getClassParamDeclareList() {
    return PsiTreeUtil.getChildOfType(this, LSFClassParamDeclareList.class);
  }

  @Override
  @Nullable
  public LSFDoInputBody getDoInputBody() {
    return PsiTreeUtil.getChildOfType(this, LSFDoInputBody.class);
  }

  @Override
  @Nullable
  public LSFImportActionSourceType getImportActionSourceType() {
    return PsiTreeUtil.getChildOfType(this, LSFImportActionSourceType.class);
  }

  @Override
  @Nullable
  public LSFNonEmptyImportFieldDefinitions getNonEmptyImportFieldDefinitions() {
    return PsiTreeUtil.getChildOfType(this, LSFNonEmptyImportFieldDefinitions.class);
  }

  @Override
  @Nullable
  public LSFNonEmptyImportPropertyUsageListWithIds getNonEmptyImportPropertyUsageListWithIds() {
    return PsiTreeUtil.getChildOfType(this, LSFNonEmptyImportPropertyUsageListWithIds.class);
  }

  @Override
  @Nullable
  public LSFPropertyExpression getPropertyExpression() {
    return PsiTreeUtil.getChildOfType(this, LSFPropertyExpression.class);
  }

  @Override
  @Nullable
  public LSFPropertyUsage getPropertyUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFPropertyUsage.class);
  }

  @Override
  public Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params) {
    return LSFPsiImplUtil.inferActionParamClasses(this, params);
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
