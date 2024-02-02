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

public class LSFImportFormActionPropertyDefinitionBodyImpl extends ASTWrapperPsiElement implements LSFImportFormActionPropertyDefinitionBody {

  public LSFImportFormActionPropertyDefinitionBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitImportFormActionPropertyDefinitionBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFFormUsage getFormUsage() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFFormUsage.class));
  }

  @Override
  @NotNull
  public List<LSFGroupObjectUsage> getGroupObjectUsageList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFGroupObjectUsage.class);
  }

  @Override
  @Nullable
  public LSFImportFormHierarchicalActionSourceType getImportFormHierarchicalActionSourceType() {
    return PsiTreeUtil.getChildOfType(this, LSFImportFormHierarchicalActionSourceType.class);
  }

  @Override
  @Nullable
  public LSFImportFormPlainActionSourceType getImportFormPlainActionSourceType() {
    return PsiTreeUtil.getChildOfType(this, LSFImportFormPlainActionSourceType.class);
  }

  @Override
  @NotNull
  public List<LSFPropertyExpression> getPropertyExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFPropertyExpression.class);
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
