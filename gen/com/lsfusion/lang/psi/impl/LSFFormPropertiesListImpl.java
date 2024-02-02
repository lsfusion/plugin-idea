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

public class LSFFormPropertiesListImpl extends ASTWrapperPsiElement implements LSFFormPropertiesList {

  public LSFFormPropertiesListImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitFormPropertiesList(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFFormMappedNamePropertiesList getFormMappedNamePropertiesList() {
    return PsiTreeUtil.getChildOfType(this, LSFFormMappedNamePropertiesList.class);
  }

  @Override
  @Nullable
  public LSFFormMappedPropertiesList getFormMappedPropertiesList() {
    return PsiTreeUtil.getChildOfType(this, LSFFormMappedPropertiesList.class);
  }

  @Override
  @Nullable
  public LSFFormPropertyOptionsList getFormPropertyOptionsList() {
    return PsiTreeUtil.getChildOfType(this, LSFFormPropertyOptionsList.class);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
