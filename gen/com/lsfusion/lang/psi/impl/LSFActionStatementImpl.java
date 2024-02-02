// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.declarations.impl.LSFStatementActionDeclarationImpl;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;
import com.lsfusion.lang.psi.stubs.StatementActionStubElement;
import com.intellij.psi.stubs.IStubElementType;

public class LSFActionStatementImpl extends LSFStatementActionDeclarationImpl implements LSFActionStatement {

  public LSFActionStatementImpl(ASTNode node) {
    super(node);
  }

  public LSFActionStatementImpl(StatementActionStubElement stub, IStubElementType stubType) {
    super(stub, stubType);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitActionStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFActionUnfriendlyPD getActionUnfriendlyPD() {
    return PsiTreeUtil.getChildOfType(this, LSFActionUnfriendlyPD.class);
  }

  @Override
  @Nullable
  public LSFListActionPropertyDefinitionBody getListActionPropertyDefinitionBody() {
    return PsiTreeUtil.getChildOfType(this, LSFListActionPropertyDefinitionBody.class);
  }

  @Override
  @Nullable
  public LSFNonEmptyPropertyOptions getNonEmptyPropertyOptions() {
    return PsiTreeUtil.getChildOfType(this, LSFNonEmptyPropertyOptions.class);
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
