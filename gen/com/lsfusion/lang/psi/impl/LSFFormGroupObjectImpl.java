// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFGroupObjectDeclarationImpl;
import com.lsfusion.lang.psi.*;

public class LSFFormGroupObjectImpl extends LSFGroupObjectDeclarationImpl implements LSFFormGroupObject {

  public LSFFormGroupObjectImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitFormGroupObject(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFFormMultiGroupObjectDeclaration getFormMultiGroupObjectDeclaration() {
    return PsiTreeUtil.getChildOfType(this, LSFFormMultiGroupObjectDeclaration.class);
  }

  @Override
  @Nullable
  public LSFFormSingleGroupObjectDeclaration getFormSingleGroupObjectDeclaration() {
    return PsiTreeUtil.getChildOfType(this, LSFFormSingleGroupObjectDeclaration.class);
  }

}
