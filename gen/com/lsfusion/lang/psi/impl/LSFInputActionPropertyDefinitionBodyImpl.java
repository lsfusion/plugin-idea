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
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.typeinfer.Inferred;
import java.util.Set;

public class LSFInputActionPropertyDefinitionBodyImpl extends ASTWrapperPsiElement implements LSFInputActionPropertyDefinitionBody {

  public LSFInputActionPropertyDefinitionBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitInputActionPropertyDefinitionBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFBuiltInClassName getBuiltInClassName() {
    return PsiTreeUtil.getChildOfType(this, LSFBuiltInClassName.class);
  }

  @Override
  @Nullable
  public LSFChangeInputPropertyCustomView getChangeInputPropertyCustomView() {
    return PsiTreeUtil.getChildOfType(this, LSFChangeInputPropertyCustomView.class);
  }

  @Override
  @Nullable
  public LSFClassOrExpression getClassOrExpression() {
    return PsiTreeUtil.getChildOfType(this, LSFClassOrExpression.class);
  }

  @Override
  @Nullable
  public LSFContextActions getContextActions() {
    return PsiTreeUtil.getChildOfType(this, LSFContextActions.class);
  }

  @Override
  @Nullable
  public LSFDoInputBody getDoInputBody() {
    return PsiTreeUtil.getChildOfType(this, LSFDoInputBody.class);
  }

  @Override
  @Nullable
  public LSFFormSessionScopeClause getFormSessionScopeClause() {
    return PsiTreeUtil.getChildOfType(this, LSFFormSessionScopeClause.class);
  }

  @Override
  @Nullable
  public LSFListWhereInputProps getListWhereInputProps() {
    return PsiTreeUtil.getChildOfType(this, LSFListWhereInputProps.class);
  }

  @Override
  @Nullable
  public LSFParamDeclare getParamDeclare() {
    return PsiTreeUtil.getChildOfType(this, LSFParamDeclare.class);
  }

  @Override
  @Nullable
  public LSFPropertyExpression getPropertyExpression() {
    return PsiTreeUtil.getChildOfType(this, LSFPropertyExpression.class);
  }

  @Override
  @Nullable
  public LSFStaticDestination getStaticDestination() {
    return PsiTreeUtil.getChildOfType(this, LSFStaticDestination.class);
  }

  @Override
  public Inferred inferActionParamClasses(@Nullable Set<LSFExprParamDeclaration> params) {
    return LSFPsiImplUtil.inferActionParamClasses(this, params);
  }

  @Override
  public @Nullable LSFClassSet resolveClass() {
    return LSFPsiImplUtil.resolveClass(this);
  }

  @Override
  public void ensureClass(@NotNull LSFValueClass valueClass, MetaTransaction metaTrans) {
    LSFPsiImplUtil.ensureClass(this, valueClass, metaTrans);
  }

  @Override
  public ContextModifier getDoContextModifier() {
    return LSFPsiImplUtil.getDoContextModifier(this);
  }

  @Override
  public ContextInferrer getDoContextInferrer() {
    return LSFPsiImplUtil.getDoContextInferrer(this);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
