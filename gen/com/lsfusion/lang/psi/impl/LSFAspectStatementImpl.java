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
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;
import javax.swing.Icon;

public class LSFAspectStatementImpl extends ASTWrapperPsiElement implements LSFAspectStatement {

  public LSFAspectStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitAspectStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFActionPropertyDefinitionBody getActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFAspectAfter getAspectAfter() {
    return PsiTreeUtil.getChildOfType(this, LSFAspectAfter.class);
  }

  @Override
  @Nullable
  public LSFAspectBefore getAspectBefore() {
    return PsiTreeUtil.getChildOfType(this, LSFAspectBefore.class);
  }

  @Override
  @Nullable
  public LSFMappedActionClassParamDeclare getMappedActionClassParamDeclare() {
    return PsiTreeUtil.getChildOfType(this, LSFMappedActionClassParamDeclare.class);
  }

  @Override
  public ContextModifier getContextModifier() {
    return LSFPsiImplUtil.getContextModifier(this);
  }

  @Override
  public ContextInferrer getContextInferrer() {
    return LSFPsiImplUtil.getContextInferrer(this);
  }

  @Override
  public @Nullable Icon getIcon(int flags) {
    return LSFPsiImplUtil.getIcon(this, flags);
  }

}
