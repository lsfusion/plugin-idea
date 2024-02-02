// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFStaticObjectDeclarationImpl;
import com.lsfusion.lang.psi.*;

public class LSFStaticObjectDeclImpl extends LSFStaticObjectDeclarationImpl implements LSFStaticObjectDecl {

  public LSFStaticObjectDeclImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitStaticObjectDecl(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFSimpleNameWithCaption getSimpleNameWithCaption() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFSimpleNameWithCaption.class));
  }

}
