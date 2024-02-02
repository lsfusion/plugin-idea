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

public class LSFInternalStatementImpl extends ASTWrapperPsiElement implements LSFInternalStatement {

  public LSFInternalStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitInternalStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFInternalAction getInternalAction() {
    return PsiTreeUtil.getChildOfType(this, LSFInternalAction.class);
  }

  @Override
  @Nullable
  public LSFInternalClass getInternalClass() {
    return PsiTreeUtil.getChildOfType(this, LSFInternalClass.class);
  }

  @Override
  @Nullable
  public LSFInternalFormObject getInternalFormObject() {
    return PsiTreeUtil.getChildOfType(this, LSFInternalFormObject.class);
  }

  @Override
  @Nullable
  public LSFInternalModule getInternalModule() {
    return PsiTreeUtil.getChildOfType(this, LSFInternalModule.class);
  }

  @Override
  @Nullable
  public LSFInternalProperty getInternalProperty() {
    return PsiTreeUtil.getChildOfType(this, LSFInternalProperty.class);
  }

  @Override
  @Nullable
  public LSFInternalPropertyDraw getInternalPropertyDraw() {
    return PsiTreeUtil.getChildOfType(this, LSFInternalPropertyDraw.class);
  }

}
