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
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.Inferred;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import java.util.Set;

public class LSFPropertyExpressionImpl extends ASTWrapperPsiElement implements LSFPropertyExpression {

  public LSFPropertyExpressionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitPropertyExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFIfPE getIfPE() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFIfPE.class));
  }

  @Override
  public @Nullable LSFExClassSet resolveValueClass(boolean infer) {
    return LSFPsiImplUtil.resolveValueClass(this, infer);
  }

  @Override
  public @NotNull List<LSFExprParamDeclaration> resolveParams() {
    return LSFPsiImplUtil.resolveParams(this);
  }

  @Override
  public @NotNull Set<String> resolveAllParams() {
    return LSFPsiImplUtil.resolveAllParams(this);
  }

  @Override
  public @Nullable LSFExClassSet resolveInferredValueClass(@Nullable InferExResult inferred) {
    return LSFPsiImplUtil.resolveInferredValueClass(this, inferred);
  }

  @Override
  public @NotNull Inferred inferParamClasses(@Nullable LSFExClassSet valueClass) {
    return LSFPsiImplUtil.inferParamClasses(this, valueClass);
  }

  @Override
  public List<String> getValueClassNames() {
    return LSFPsiImplUtil.getValueClassNames(this);
  }

  @Override
  public List<String> getValuePropertyNames() {
    return LSFPsiImplUtil.getValuePropertyNames(this);
  }

}
