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

public class LSFObjectInputPropsImpl extends ASTWrapperPsiElement implements LSFObjectInputProps {

  public LSFObjectInputPropsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitObjectInputProps(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFChangeInput getChangeInput() {
    return PsiTreeUtil.getChildOfType(this, LSFChangeInput.class);
  }

  @Override
  @Nullable
  public LSFConstraintFilter getConstraintFilter() {
    return PsiTreeUtil.getChildOfType(this, LSFConstraintFilter.class);
  }

  @Override
  @Nullable
  public LSFObjectListInputProps getObjectListInputProps() {
    return PsiTreeUtil.getChildOfType(this, LSFObjectListInputProps.class);
  }

  @Override
  @Nullable
  public LSFSimpleName getSimpleName() {
    return PsiTreeUtil.getChildOfType(this, LSFSimpleName.class);
  }

  @Override
  @Nullable
  public LSFStaticDestination getStaticDestination() {
    return PsiTreeUtil.getChildOfType(this, LSFStaticDestination.class);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
