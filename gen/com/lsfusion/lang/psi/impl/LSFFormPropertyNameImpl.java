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

public class LSFFormPropertyNameImpl extends ASTWrapperPsiElement implements LSFFormPropertyName {

  public LSFFormPropertyNameImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitFormPropertyName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFActionUsage getActionUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFActionUsage.class);
  }

  @Override
  @Nullable
  public LSFPredefinedFormPropertyName getPredefinedFormPropertyName() {
    return PsiTreeUtil.getChildOfType(this, LSFPredefinedFormPropertyName.class);
  }

  @Override
  @Nullable
  public LSFPropertyElseActionUsage getPropertyElseActionUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFPropertyElseActionUsage.class);
  }

}
