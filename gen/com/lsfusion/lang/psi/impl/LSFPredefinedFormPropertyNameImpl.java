// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.LSFIdImpl;
import com.lsfusion.lang.psi.*;

public class LSFPredefinedFormPropertyNameImpl extends LSFIdImpl implements LSFPredefinedFormPropertyName {

  public LSFPredefinedFormPropertyNameImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitPredefinedFormPropertyName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFExplicitPropClass getExplicitPropClass() {
    return PsiTreeUtil.getChildOfType(this, LSFExplicitPropClass.class);
  }

  @Override
  @Nullable
  public LSFPredefinedAddPropertyName getPredefinedAddPropertyName() {
    return PsiTreeUtil.getChildOfType(this, LSFPredefinedAddPropertyName.class);
  }

}
