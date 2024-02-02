// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.LSFLazyParsableElement;
import com.lsfusion.lang.psi.*;
import com.intellij.psi.tree.IElementType;

public class LSFLazyScriptStatementImpl extends LSFLazyParsableElement implements LSFLazyScriptStatement {

  public LSFLazyScriptStatementImpl(ASTNode node) {
    super(node);
  }

  public LSFLazyScriptStatementImpl(@NotNull IElementType elementType, CharSequence buffer) {
    super(elementType, buffer);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitLazyScriptStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<LSFScriptStatement> getScriptStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFScriptStatement.class);
  }

}
