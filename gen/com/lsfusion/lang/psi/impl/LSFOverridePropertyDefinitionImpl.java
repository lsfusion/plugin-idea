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
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.Inferred;
import com.lsfusion.lang.typeinfer.LSFExClassSet;

public class LSFOverridePropertyDefinitionImpl extends ASTWrapperPsiElement implements LSFOverridePropertyDefinition {

  public LSFOverridePropertyDefinitionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitOverridePropertyDefinition(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFExclusiveOperator getExclusiveOperator() {
    return PsiTreeUtil.getChildOfType(this, LSFExclusiveOperator.class);
  }

  @Override
  @Nullable
  public LSFNonEmptyPropertyExpressionList getNonEmptyPropertyExpressionList() {
    return PsiTreeUtil.getChildOfType(this, LSFNonEmptyPropertyExpressionList.class);
  }

  @Override
  @Nullable
  public LSFOverrideOperator getOverrideOperator() {
    return PsiTreeUtil.getChildOfType(this, LSFOverrideOperator.class);
  }

  @Override
  public @Nullable LSFExClassSet resolveInferredValueClass(@Nullable InferExResult inferred) {
    return LSFPsiImplUtil.resolveInferredValueClass(this, inferred);
  }

  @Override
  public @NotNull Inferred inferParamClasses(@Nullable LSFExClassSet valueClass) {
    return LSFPsiImplUtil.inferParamClasses(this, valueClass);
  }

  @Override
  public List<String> getValueClassNames() {
    return LSFPsiImplUtil.getValueClassNames(this);
  }

  @Override
  public List<String> getValuePropertyNames() {
    return LSFPsiImplUtil.getValuePropertyNames(this);
  }

}
