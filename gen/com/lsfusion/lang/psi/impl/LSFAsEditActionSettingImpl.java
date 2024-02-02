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

public class LSFAsEditActionSettingImpl extends ASTWrapperPsiElement implements LSFAsEditActionSetting {

  public LSFAsEditActionSettingImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitAsEditActionSetting(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFFormEventType getFormEventType() {
    return PsiTreeUtil.getChildOfType(this, LSFFormEventType.class);
  }

  @Override
  @Nullable
  public LSFNoContextActionOrPropertyUsage getNoContextActionOrPropertyUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFNoContextActionOrPropertyUsage.class);
  }

}
