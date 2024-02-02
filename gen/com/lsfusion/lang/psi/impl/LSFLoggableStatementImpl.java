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

public class LSFLoggableStatementImpl extends ASTWrapperPsiElement implements LSFLoggableStatement {

  public LSFLoggableStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitLoggableStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFNonEmptyNoContextPropertyUsageList getNonEmptyNoContextPropertyUsageList() {
    return PsiTreeUtil.getChildOfType(this, LSFNonEmptyNoContextPropertyUsageList.class);
  }

  @Override
  public @Nullable Icon getIcon(int flags) {
    return LSFPsiImplUtil.getIcon(this, flags);
  }

}
