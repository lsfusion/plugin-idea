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
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import java.util.Set;

public class LSFActivateActionPropertyDefinitionBodyImpl extends ASTWrapperPsiElement implements LSFActivateActionPropertyDefinitionBody {

  public LSFActivateActionPropertyDefinitionBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitActivateActionPropertyDefinitionBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFComponentID getComponentID() {
    return PsiTreeUtil.getChildOfType(this, LSFComponentID.class);
  }

  @Override
  @Nullable
  public LSFFormPropertyDrawID getFormPropertyDrawID() {
    return PsiTreeUtil.getChildOfType(this, LSFFormPropertyDrawID.class);
  }

  @Override
  @Nullable
  public LSFFormUsage getFormUsage() {
    return PsiTreeUtil.getChildOfType(this, LSFFormUsage.class);
  }

  @Override
  public Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params) {
    return LSFPsiImplUtil.inferActionParamClasses(this, params);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
