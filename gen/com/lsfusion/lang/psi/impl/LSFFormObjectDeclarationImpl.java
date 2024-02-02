// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFObjectDeclarationImpl;
import com.lsfusion.lang.psi.*;

public class LSFFormObjectDeclarationImpl extends LSFObjectDeclarationImpl implements LSFFormObjectDeclaration {

  public LSFFormObjectDeclarationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitFormObjectDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFClassName getClassName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFClassName.class));
  }

  @Override
  @Nullable
  public LSFFormActionPropertyObject getFormActionPropertyObject() {
    return PsiTreeUtil.getChildOfType(this, LSFFormActionPropertyObject.class);
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
