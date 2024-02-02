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

public class LSFComponentSelectorImpl extends ASTWrapperPsiElement implements LSFComponentSelector {

  public LSFComponentSelectorImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitComponentSelector(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFComponentSelector getComponentSelector() {
    return PsiTreeUtil.getChildOfType(this, LSFComponentSelector.class);
  }

  @Override
  @Nullable
  public LSFComponentUsage getComponentUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFComponentUsage.class);
  }

  @Override
  @Nullable
  public LSFFilterGroupSelector getFilterGroupSelector() {
    return PsiTreeUtil.getChildOfType(this, LSFFilterGroupSelector.class);
  }

  @Override
  @Nullable
  public LSFFilterPropertySelector getFilterPropertySelector() {
    return PsiTreeUtil.getChildOfType(this, LSFFilterPropertySelector.class);
  }

  @Override
  @Nullable
  public LSFGlobalSingleSelectorType getGlobalSingleSelectorType() {
    return PsiTreeUtil.getChildOfType(this, LSFGlobalSingleSelectorType.class);
  }

  @Override
  @Nullable
  public LSFGroupObjectSelector getGroupObjectSelector() {
    return PsiTreeUtil.getChildOfType(this, LSFGroupObjectSelector.class);
  }

  @Override
  @Nullable
  public LSFGroupObjectTreeSingleSelectorType getGroupObjectTreeSingleSelectorType() {
    return PsiTreeUtil.getChildOfType(this, LSFGroupObjectTreeSingleSelectorType.class);
  }

  @Override
  @Nullable
  public LSFGroupSelector getGroupSelector() {
    return PsiTreeUtil.getChildOfType(this, LSFGroupSelector.class);
  }

  @Override
  @Nullable
  public LSFGroupSingleSelectorType getGroupSingleSelectorType() {
    return PsiTreeUtil.getChildOfType(this, LSFGroupSingleSelectorType.class);
  }

  @Override
  @Nullable
  public LSFPropertySelector getPropertySelector() {
    return PsiTreeUtil.getChildOfType(this, LSFPropertySelector.class);
  }

  @Override
  @Nullable
  public LSFTreeGroupSelector getTreeGroupSelector() {
    return PsiTreeUtil.getChildOfType(this, LSFTreeGroupSelector.class);
  }

}
