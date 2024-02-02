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

public class LSFNavigatorElementBodyStatementImpl extends ASTWrapperPsiElement implements LSFNavigatorElementBodyStatement {

  public LSFNavigatorElementBodyStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitNavigatorElementBodyStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFEmptyStatement getEmptyStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFEmptyStatement.class);
  }

  @Override
  @Nullable
  public LSFMoveNavigatorElementStatement getMoveNavigatorElementStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFMoveNavigatorElementStatement.class);
  }

  @Override
  @Nullable
  public LSFNeStub getNeStub() {
    return PsiTreeUtil.getChildOfType(this, LSFNeStub.class);
  }

  @Override
  @Nullable
  public LSFNewNavigatorElementStatement getNewNavigatorElementStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFNewNavigatorElementStatement.class);
  }

  @Override
  @Nullable
  public LSFSetupNavigatorElementStatement getSetupNavigatorElementStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFSetupNavigatorElementStatement.class);
  }

}
