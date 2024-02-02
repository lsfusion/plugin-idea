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

public class LSFTerminalFlowActionPropertyDefinitionBodyImpl extends ASTWrapperPsiElement implements LSFTerminalFlowActionPropertyDefinitionBody {

  public LSFTerminalFlowActionPropertyDefinitionBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitTerminalFlowActionPropertyDefinitionBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFBreakActionOperator getBreakActionOperator() {
    return PsiTreeUtil.getChildOfType(this, LSFBreakActionOperator.class);
  }

  @Override
  @Nullable
  public LSFContinueActionOperator getContinueActionOperator() {
    return PsiTreeUtil.getChildOfType(this, LSFContinueActionOperator.class);
  }

  @Override
  @Nullable
  public LSFReturnActionOperator getReturnActionOperator() {
    return PsiTreeUtil.getChildOfType(this, LSFReturnActionOperator.class);
  }

  @Override
  public Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params) {
    return LSFPsiImplUtil.inferActionParamClasses(this, params);
  }

}
