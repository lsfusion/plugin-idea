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

public class LSFNewComponentStatementImpl extends ASTWrapperPsiElement implements LSFNewComponentStatement {

  public LSFNewComponentStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitNewComponentStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFComponentBody getComponentBody() {
    return PsiTreeUtil.getChildOfType(this, LSFComponentBody.class);
  }

  @Override
  @NotNull
  public LSFComponentDecl getComponentDecl() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFComponentDecl.class));
  }

  @Override
  @Nullable
  public LSFComponentInsertPosition getComponentInsertPosition() {
    return PsiTreeUtil.getChildOfType(this, LSFComponentInsertPosition.class);
  }

}
