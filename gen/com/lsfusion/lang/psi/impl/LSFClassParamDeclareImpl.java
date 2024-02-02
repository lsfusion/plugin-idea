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
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.meta.MetaTransaction;

public class LSFClassParamDeclareImpl extends ASTWrapperPsiElement implements LSFClassParamDeclare {

  public LSFClassParamDeclareImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitClassParamDeclare(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFAggrParamPropDeclare getAggrParamPropDeclare() {
    return PsiTreeUtil.getChildOfType(this, LSFAggrParamPropDeclare.class);
  }

  @Override
  @Nullable
  public LSFUntypedParamDeclare getUntypedParamDeclare() {
    return PsiTreeUtil.getChildOfType(this, LSFUntypedParamDeclare.class);
  }

  @Override
  public @Nullable LSFClassSet resolveClass() {
    return LSFPsiImplUtil.resolveClass(this);
  }

  @Override
  public void ensureClass(@NotNull LSFValueClass valueClass, MetaTransaction metaTrans) {
    LSFPsiImplUtil.ensureClass(this, valueClass, metaTrans);
  }

  @Override
  public LSFClassName getClassName() {
    return LSFPsiImplUtil.getClassName(this);
  }

  @Override
  public @NotNull LSFParamDeclare getParamDeclare() {
    return LSFPsiImplUtil.getParamDeclare(this);
  }

}
