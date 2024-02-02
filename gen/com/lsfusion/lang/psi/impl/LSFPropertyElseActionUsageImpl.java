// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.references.impl.LSFPropElseActionReferenceImpl;
import com.lsfusion.lang.psi.*;

public class LSFPropertyElseActionUsageImpl extends LSFPropElseActionReferenceImpl implements LSFPropertyElseActionUsage {

  public LSFPropertyElseActionUsageImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitPropertyElseActionUsage(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFCompoundID getCompoundID() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFCompoundID.class));
  }

  @Override
  @Nullable
  public LSFExplicitPropClassUsage getExplicitPropClassUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFExplicitPropClassUsage.class);
  }

}
