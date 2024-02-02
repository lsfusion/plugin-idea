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

public class LSFComponentPropertyValueImpl extends ASTWrapperPsiElement implements LSFComponentPropertyValue {

  public LSFComponentPropertyValueImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitComponentPropertyValue(this);
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
  public LSFBoundsDoubleLiteral getBoundsDoubleLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFBoundsDoubleLiteral.class);
  }

  @Override
  @Nullable
  public LSFBoundsIntLiteral getBoundsIntLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFBoundsIntLiteral.class);
  }

  @Override
  @Nullable
  public LSFContainerTypeLiteral getContainerTypeLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFContainerTypeLiteral.class);
  }

  @Override
  @Nullable
  public LSFDesignCalcPropertyObject getDesignCalcPropertyObject() {
    return PsiTreeUtil.getChildOfType(this, LSFDesignCalcPropertyObject.class);
  }

  @Override
  @Nullable
  public LSFDimensionLiteral getDimensionLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFDimensionLiteral.class);
  }

  @Override
  @Nullable
  public LSFFlexAlignmentLiteral getFlexAlignmentLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFFlexAlignmentLiteral.class);
  }

  @Override
  @Nullable
  public LSFTbooleanLiteral getTbooleanLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFTbooleanLiteral.class);
  }

}
