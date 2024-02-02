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
import com.lsfusion.lang.classes.LSFClassSet;

public class LSFEmailPropertyUsageImpl extends ASTWrapperPsiElement implements LSFEmailPropertyUsage {

  public LSFEmailPropertyUsageImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitEmailPropertyUsage(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFPropertyUsage getPropertyUsage() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFPropertyUsage.class));
  }

  @Override
  public List<LSFClassSet> resolveParamClasses() {
    return LSFPsiImplUtil.resolveParamClasses(this);
  }

  @Override
  public @Nullable PsiElement getParamList() {
    return LSFPsiImplUtil.getParamList(this);
  }

}
