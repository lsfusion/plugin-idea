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

public class LSFForAddObjClauseImpl extends ASTWrapperPsiElement implements LSFForAddObjClause {

  public LSFForAddObjClauseImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitForAddObjClause(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFCustomClassUsage getCustomClassUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFCustomClassUsage.class);
  }

  @Override
  @Nullable
  public LSFParamDeclare getParamDeclare() {
    return PsiTreeUtil.getChildOfType(this, LSFParamDeclare.class);
  }

  @Override
  public @Nullable LSFClassSet resolveClass() {
    return LSFPsiImplUtil.resolveClass(this);
  }

  @Override
  public void ensureClass(@NotNull LSFValueClass valueClass, MetaTransaction metaTrans) {
    LSFPsiImplUtil.ensureClass(this, valueClass, metaTrans);
  }

}
