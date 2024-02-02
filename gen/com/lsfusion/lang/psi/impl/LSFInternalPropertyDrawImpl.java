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
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;

public class LSFInternalPropertyDrawImpl extends ASTWrapperPsiElement implements LSFInternalPropertyDraw {

  public LSFInternalPropertyDrawImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitInternalPropertyDraw(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFFormPropertyDrawUsage getFormPropertyDrawUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFFormPropertyDrawUsage.class);
  }

  @Override
  @Nullable
  public LSFFormUsage getFormUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFFormUsage.class);
  }

  @Override
  public @Nullable LSFFormDeclaration resolveFormDecl() {
    return LSFPsiImplUtil.resolveFormDecl(this);
  }

}
