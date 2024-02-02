// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.LSFListActionImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import java.util.Set;

public class LSFListActionPropertyDefinitionBodyImpl extends LSFListActionImpl implements LSFListActionPropertyDefinitionBody {

  public LSFListActionPropertyDefinitionBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitListActionPropertyDefinitionBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<LSFActionPropertyDefinitionBody> getActionPropertyDefinitionBodyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFActionPropertyDefinitionBody.class);
  }

  @Override
  @NotNull
  public List<LSFLocalDataPropertyDefinition> getLocalDataPropertyDefinitionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFLocalDataPropertyDefinition.class);
  }

  @Override
  public Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params) {
    return LSFPsiImplUtil.inferActionParamClasses(this, params);
  }

  @Override
  public @NotNull Set<String> resolveAllParams() {
    return LSFPsiImplUtil.resolveAllParams(this);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
