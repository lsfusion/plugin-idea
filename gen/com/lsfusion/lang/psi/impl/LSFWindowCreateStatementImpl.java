// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFWindowDeclarationImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;
import com.lsfusion.lang.psi.stubs.WindowStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFWindowCreateStatementImpl extends LSFWindowDeclarationImpl implements LSFWindowCreateStatement {

  public LSFWindowCreateStatementImpl(ASTNode node) {
    super(node);
  }

  public LSFWindowCreateStatementImpl(WindowStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitWindowCreateStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFLocalizedStringLiteral getLocalizedStringLiteral() {
    return PsiTreeUtil.getChildOfType(this, LSFLocalizedStringLiteral.class);
  }

  @Override
  @NotNull
  public LSFSimpleName getSimpleName() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFSimpleName.class));
  }

  @Override
  @Nullable
  public LSFWindowOptions getWindowOptions() {
    return PsiTreeUtil.getChildOfType(this, LSFWindowOptions.class);
  }

  @Override
  @Nullable
  public LSFWindowType getWindowType() {
    return PsiTreeUtil.getChildOfType(this, LSFWindowType.class);
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
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
