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
import com.lsfusion.lang.typeinfer.Inferred;
import java.util.Set;

public class LSFNewSessionActionPropertyDefinitionBodyImpl extends ASTWrapperPsiElement implements LSFNewSessionActionPropertyDefinitionBody {

  public LSFNewSessionActionPropertyDefinitionBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitNewSessionActionPropertyDefinitionBody(this);
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
  public LSFNestedSessionOperator getNestedSessionOperator() {
    return PsiTreeUtil.getChildOfType(this, LSFNestedSessionOperator.class);
  }

  @Override
  @Nullable
  public LSFNewSessionOperator getNewSessionOperator() {
    return PsiTreeUtil.getChildOfType(this, LSFNewSessionOperator.class);
  }

  @Override
  @Nullable
  public LSFNonEmptyFormUsageList getNonEmptyFormUsageList() {
    return PsiTreeUtil.getChildOfType(this, LSFNonEmptyFormUsageList.class);
  }

  @Override
  @Nullable
  public LSFNonEmptyNoContextPropertyUsageList getNonEmptyNoContextPropertyUsageList() {
    return PsiTreeUtil.getChildOfType(this, LSFNonEmptyNoContextPropertyUsageList.class);
  }

  @Override
  public Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params) {
    return LSFPsiImplUtil.inferActionParamClasses(this, params);
  }

}
