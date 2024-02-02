// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.references.impl.LSFPropertyDrawReferenceImpl;
import com.lsfusion.lang.psi.*;

public class LSFFormPropertyDrawUsageImpl extends LSFPropertyDrawReferenceImpl implements LSFFormPropertyDrawUsage {

  public LSFFormPropertyDrawUsageImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitFormPropertyDrawUsage(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFAliasUsage getAliasUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFAliasUsage.class);
  }

  @Override
  @Nullable
  public LSFFormPropertyDrawPropertyUsage getFormPropertyDrawPropertyUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFFormPropertyDrawPropertyUsage.class);
  }

  @Override
  @Nullable
  public LSFObjectUsageList getObjectUsageList() {
    return PsiTreeUtil.getChildOfType(this, LSFObjectUsageList.class);
  }

}
