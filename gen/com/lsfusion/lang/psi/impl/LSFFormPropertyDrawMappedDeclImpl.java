// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFPropertyDrawMappedDeclarationImpl;
import com.lsfusion.lang.psi.*;

public class LSFFormPropertyDrawMappedDeclImpl extends LSFPropertyDrawMappedDeclarationImpl implements LSFFormPropertyDrawMappedDecl {

  public LSFFormPropertyDrawMappedDeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitFormPropertyDrawMappedDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFFormActionDeclaration getFormActionDeclaration() {
    return PsiTreeUtil.getChildOfType(this, LSFFormActionDeclaration.class);
  }

  @Override
  @Nullable
  public LSFFormExprDeclaration getFormExprDeclaration() {
    return PsiTreeUtil.getChildOfType(this, LSFFormExprDeclaration.class);
  }

  @Override
  @Nullable
  public LSFFormPropertyDrawObject getFormPropertyDrawObject() {
    return PsiTreeUtil.getChildOfType(this, LSFFormPropertyDrawObject.class);
  }

  @Override
  @Nullable
  public LSFFormPropertyOptionsList getFormPropertyOptionsList() {
    return PsiTreeUtil.getChildOfType(this, LSFFormPropertyOptionsList.class);
  }

  @Override
  @Nullable
  public LSFLocalizedStringLiteral getLocalizedStringLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFLocalizedStringLiteral.class);
  }

  @Override
  @Nullable
  public LSFSimpleName getSimpleName() {
    return PsiTreeUtil.getChildOfType(this, LSFSimpleName.class);
  }

}
