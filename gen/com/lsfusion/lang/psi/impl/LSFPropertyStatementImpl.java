// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFStatementGlobalPropDeclarationImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;
import com.lsfusion.lang.psi.stubs.StatementPropStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFPropertyStatementImpl extends LSFStatementGlobalPropDeclarationImpl implements LSFPropertyStatement {

  public LSFPropertyStatementImpl(ASTNode node) {
    super(node);
  }

  public LSFPropertyStatementImpl(StatementPropStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitPropertyStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public LSFEqualsSign getEqualsSign() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFEqualsSign.class));
  }

  @Override
  @Nullable
  public LSFNonEmptyPropertyOptions getNonEmptyPropertyOptions() {
    return PsiTreeUtil.getChildOfType(this, LSFNonEmptyPropertyOptions.class);
  }

  @Override
  @NotNull
  public LSFPropertyCalcStatement getPropertyCalcStatement() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFPropertyCalcStatement.class));
  }

  @Override
  @NotNull
  public LSFPropertyDeclaration getPropertyDeclaration() {
    return notNullChild(PsiTreeUtil.getChildOfType(this, LSFPropertyDeclaration.class));
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
