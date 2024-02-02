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

public class LSFFormEventTypeImpl extends ASTWrapperPsiElement implements LSFFormEventType {

  public LSFFormEventTypeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitFormEventType(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFContextMenuEventType getContextMenuEventType() {
    return PsiTreeUtil.getChildOfType(this, LSFContextMenuEventType.class);
  }

  @Override
  @Nullable
  public LSFKeyPressedEventType getKeyPressedEventType() {
    return PsiTreeUtil.getChildOfType(this, LSFKeyPressedEventType.class);
  }

}
