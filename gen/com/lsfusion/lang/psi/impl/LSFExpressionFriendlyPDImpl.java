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

public class LSFExpressionFriendlyPDImpl extends ASTWrapperPsiElement implements LSFExpressionFriendlyPD {

  public LSFExpressionFriendlyPDImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitExpressionFriendlyPD(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFActiveTabPropertyDefinition getActiveTabPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFActiveTabPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFCasePropertyDefinition getCasePropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFCasePropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFCastPropertyDefinition getCastPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFCastPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFConcatPropertyDefinition getConcatPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFConcatPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFExpressionLiteral getExpressionLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFExpressionLiteral.class);
  }

  @Override
  @Nullable
  public LSFGroupExprPropertyDefinition getGroupExprPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFGroupExprPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFIfElsePropertyDefinition getIfElsePropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFIfElsePropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFJoinPropertyDefinition getJoinPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFJoinPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFJsonFormPropertyDefinition getJsonFormPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFJsonFormPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFJsonPropertyDefinition getJsonPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFJsonPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFMaxPropertyDefinition getMaxPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFMaxPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFMultiPropertyDefinition getMultiPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFMultiPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFOverridePropertyDefinition getOverridePropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFOverridePropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFPartitionPropertyDefinition getPartitionPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFPartitionPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFRecursivePropertyDefinition getRecursivePropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFRecursivePropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFRoundPropertyDefinition getRoundPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFRoundPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFSessionPropertyDefinition getSessionPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFSessionPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFSignaturePropertyDefinition getSignaturePropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFSignaturePropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFStructCreationPropertyDefinition getStructCreationPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFStructCreationPropertyDefinition.class);
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
