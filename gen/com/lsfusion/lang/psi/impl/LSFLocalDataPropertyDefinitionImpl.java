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

public class LSFLocalDataPropertyDefinitionImpl extends ASTWrapperPsiElement implements LSFLocalDataPropertyDefinition {

  public LSFLocalDataPropertyDefinitionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitLocalDataPropertyDefinition(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFClassName getClassName() {
    return PsiTreeUtil.getChildOfType(this, LSFClassName.class);
  }

  @Override
  @Nullable
  public LSFClassNameList getClassNameList() {
    return PsiTreeUtil.getChildOfType(this, LSFClassNameList.class);
  }

  @Override
  @Nullable
  public LSFNestedLocalModifier getNestedLocalModifier() {
    return PsiTreeUtil.getChildOfType(this, LSFNestedLocalModifier.class);
  }

  @Override
  @Nullable
  public LSFNonEmptyLocalPropertyDeclarationNameList getNonEmptyLocalPropertyDeclarationNameList() {
    return PsiTreeUtil.getChildOfType(this, LSFNonEmptyLocalPropertyDeclarationNameList.class);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
