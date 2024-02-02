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

public class LSFMetaCodeBodyImpl extends ASTWrapperPsiElement implements LSFMetaCodeBody {

  public LSFMetaCodeBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitMetaCodeBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<LSFLazyMetaStatement> getLazyMetaStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFLazyMetaStatement.class);
  }

  @Override
  @NotNull
  public LSFMetaCodeBodyLeftBrace getMetaCodeBodyLeftBrace() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFMetaCodeBodyLeftBrace.class));
  }

  @Override
  @NotNull
  public LSFMetaCodeBodyRightBrace getMetaCodeBodyRightBrace() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFMetaCodeBodyRightBrace.class));
  }

}
