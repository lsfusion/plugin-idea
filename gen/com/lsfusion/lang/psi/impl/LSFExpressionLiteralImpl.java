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

public class LSFExpressionLiteralImpl extends ASTWrapperPsiElement implements LSFExpressionLiteral {

  public LSFExpressionLiteralImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitExpressionLiteral(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFBooleanLiteral getBooleanLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFBooleanLiteral.class);
  }

  @Override
  @Nullable
  public LSFColorLiteral getColorLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFColorLiteral.class);
  }

  @Override
  @Nullable
  public LSFDateLiteral getDateLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFDateLiteral.class);
  }

  @Override
  @Nullable
  public LSFDateTimeLiteral getDateTimeLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFDateTimeLiteral.class);
  }

  @Override
  @Nullable
  public LSFExpressionStringLiteral getExpressionStringLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFExpressionStringLiteral.class);
  }

  @Override
  @Nullable
  public LSFNullLiteral getNullLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFNullLiteral.class);
  }

  @Override
  @Nullable
  public LSFStaticObjectID getStaticObjectID() {
    return PsiTreeUtil.getChildOfType(this, LSFStaticObjectID.class);
  }

  @Override
  @Nullable
  public LSFTbooleanLiteral getTbooleanLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFTbooleanLiteral.class);
  }

  @Override
  @Nullable
  public LSFTimeLiteral getTimeLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFTimeLiteral.class);
  }

  @Override
  @Nullable
  public LSFUdoubleLiteral getUdoubleLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFUdoubleLiteral.class);
  }

  @Override
  @Nullable
  public LSFUintLiteral getUintLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFUintLiteral.class);
  }

  @Override
  @Nullable
  public LSFUlongLiteral getUlongLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFUlongLiteral.class);
  }

  @Override
  @Nullable
  public LSFUnumericLiteral getUnumericLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFUnumericLiteral.class);
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

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
