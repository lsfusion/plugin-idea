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
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;

public class LSFPropertyDeclarationImpl extends ASTWrapperPsiElement implements LSFPropertyDeclaration {

  public LSFPropertyDeclarationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitPropertyDeclaration(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFPropertyDeclParams getPropertyDeclParams() {
    return PsiTreeUtil.getChildOfType(this, LSFPropertyDeclParams.class);
  }

  @Override
  @NotNull
  public LSFSimpleNameWithCaption getSimpleNameWithCaption() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFSimpleNameWithCaption.class));
  }

  @Override
  public @Nullable List<LSFParamDeclaration> resolveParamDecls() {
    return LSFPsiImplUtil.resolveParamDecls(this);
  }

}
