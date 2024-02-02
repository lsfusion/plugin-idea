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

public class LSFComponentStatementImpl extends ASTWrapperPsiElement implements LSFComponentStatement {

  public LSFComponentStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitComponentStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFComponentStubStatement getComponentStubStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFComponentStubStatement.class);
  }

  @Override
  @Nullable
  public LSFEmptyStatement getEmptyStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFEmptyStatement.class);
  }

  @Override
  @Nullable
  public LSFMoveComponentStatement getMoveComponentStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFMoveComponentStatement.class);
  }

  @Override
  @Nullable
  public LSFNewComponentStatement getNewComponentStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFNewComponentStatement.class);
  }

  @Override
  @Nullable
  public LSFRemoveComponentStatement getRemoveComponentStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFRemoveComponentStatement.class);
  }

  @Override
  @Nullable
  public LSFSetObjectPropertyStatement getSetObjectPropertyStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFSetObjectPropertyStatement.class);
  }

  @Override
  @Nullable
  public LSFSetupComponentStatement getSetupComponentStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFSetupComponentStatement.class);
  }

}
