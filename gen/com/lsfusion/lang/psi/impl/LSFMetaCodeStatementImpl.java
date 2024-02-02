// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.references.impl.LSFMetaReferenceImpl;
import com.lsfusion.lang.psi.*;

public class LSFMetaCodeStatementImpl extends LSFMetaReferenceImpl implements LSFMetaCodeStatement {

  public LSFMetaCodeStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitMetaCodeStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFMetaCodeBody getMetaCodeBody() {
    return PsiTreeUtil.getChildOfType(this, LSFMetaCodeBody.class);
  }

  @Override
  @NotNull
  public LSFMetaCodeStatementHeader getMetaCodeStatementHeader() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFMetaCodeStatementHeader.class));
  }

  @Override
  @Nullable
  public LSFMetaCodeStatementSemi getMetaCodeStatementSemi() {
    return PsiTreeUtil.getChildOfType(this, LSFMetaCodeStatementSemi.class);
  }

  @Override
  public boolean isInline() {
    return LSFPsiImplUtil.isInline(this);
  }

}
