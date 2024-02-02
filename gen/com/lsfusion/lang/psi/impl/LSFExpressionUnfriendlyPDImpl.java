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
import com.intellij.openapi.util.Pair;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import java.util.Map;

public class LSFExpressionUnfriendlyPDImpl extends ASTWrapperPsiElement implements LSFExpressionUnfriendlyPD {

  public LSFExpressionUnfriendlyPDImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitExpressionUnfriendlyPD(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFAbstractPropertyDefinition getAbstractPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFAbstractPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFAggrPropertyDefinition getAggrPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFAggrPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFDataPropertyDefinition getDataPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFDataPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFFilterPropertyDefinition getFilterPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFFilterPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFFormulaPropertyDefinition getFormulaPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFFormulaPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFGroupPropertyDefinition getGroupPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFGroupPropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFNativePropertyDefinition getNativePropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFNativePropertyDefinition.class);
  }

  @Override
  @Nullable
  public LSFReflectionPropertyDefinition getReflectionPropertyDefinition() {
    return PsiTreeUtil.getChildOfType(this, LSFReflectionPropertyDefinition.class);
  }

  @Override
  public @Nullable LSFExClassSet resolveUnfriendValueClass(boolean infer) {
    return LSFPsiImplUtil.resolveUnfriendValueClass(this, infer);
  }

  @Override
  public @Nullable List<LSFExClassSet> resolveValueParamClasses(List<LSFParamDeclaration> declareParams) {
    return LSFPsiImplUtil.resolveValueParamClasses(this, declareParams);
  }

  @Override
  public @NotNull Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(List<LSFParamDeclaration> declareParams) {
    return LSFPsiImplUtil.checkValueParamClasses(this, declareParams);
  }

}
