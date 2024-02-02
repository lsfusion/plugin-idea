// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFPropertyDrawNameDeclarationImpl;
import com.lsfusion.lang.psi.*;

public class LSFFormPropertyDrawNameDeclImpl extends LSFPropertyDrawNameDeclarationImpl implements LSFFormPropertyDrawNameDecl {

  public LSFFormPropertyDrawNameDeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitFormPropertyDrawNameDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFFormPropertyName getFormPropertyName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFFormPropertyName.class));
  }

  @Override
  @NotNull
  public LSFFormPropertyOptionsList getFormPropertyOptionsList() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFFormPropertyOptionsList.class));
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
