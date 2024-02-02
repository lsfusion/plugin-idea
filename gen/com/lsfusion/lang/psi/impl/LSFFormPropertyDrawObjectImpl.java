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
import com.lsfusion.lang.classes.LSFClassSet;

public class LSFFormPropertyDrawObjectImpl extends ASTWrapperPsiElement implements LSFFormPropertyDrawObject {

  public LSFFormPropertyDrawObjectImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitFormPropertyDrawObject(this);
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
  public LSFObjectUsageList getObjectUsageList() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFObjectUsageList.class));
  }

  @Override
  public @Nullable List<LSFClassSet> resolveParamClasses() {
    return LSFPsiImplUtil.resolveParamClasses(this);
  }

  @Override
  public @Nullable PsiElement getParamList() {
    return LSFPsiImplUtil.getParamList(this);
  }

}
