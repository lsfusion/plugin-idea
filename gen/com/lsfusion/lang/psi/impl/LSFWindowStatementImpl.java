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
import javax.swing.Icon;

public class LSFWindowStatementImpl extends ASTWrapperPsiElement implements LSFWindowStatement {

  public LSFWindowStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitWindowStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFWindowCreateStatement getWindowCreateStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFWindowCreateStatement.class);
  }

  @Override
  @Nullable
  public LSFWindowHideStatement getWindowHideStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFWindowHideStatement.class);
  }

  @Override
  public @Nullable Icon getIcon(int flags) {
    return LSFPsiImplUtil.getIcon(this, flags);
  }

}
