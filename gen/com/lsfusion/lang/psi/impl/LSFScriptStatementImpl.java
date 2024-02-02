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

public class LSFScriptStatementImpl extends ASTWrapperPsiElement implements LSFScriptStatement {

  public LSFScriptStatementImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitScriptStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LSFAspectStatement getAspectStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFAspectStatement.class);
  }

  @Override
  @Nullable
  public LSFClassStatement getClassStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFClassStatement.class);
  }

  @Override
  @Nullable
  public LSFConstraintStatement getConstraintStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFConstraintStatement.class);
  }

  @Override
  @Nullable
  public LSFDesignStatement getDesignStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFDesignStatement.class);
  }

  @Override
  @Nullable
  public LSFEmptyStatement getEmptyStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFEmptyStatement.class);
  }

  @Override
  @Nullable
  public LSFEventStatement getEventStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFEventStatement.class);
  }

  @Override
  @Nullable
  public LSFExplicitInterfaceActStatement getExplicitInterfaceActStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFExplicitInterfaceActStatement.class);
  }

  @Override
  @Nullable
  public LSFExplicitInterfacePropertyStatement getExplicitInterfacePropertyStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFExplicitInterfacePropertyStatement.class);
  }

  @Override
  @Nullable
  public LSFFollowsStatement getFollowsStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFFollowsStatement.class);
  }

  @Override
  @Nullable
  public LSFFormStatement getFormStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFFormStatement.class);
  }

  @Override
  @Nullable
  public LSFGlobalEventStatement getGlobalEventStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFGlobalEventStatement.class);
  }

  @Override
  @Nullable
  public LSFGroupStatement getGroupStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFGroupStatement.class);
  }

  @Override
  @Nullable
  public LSFIndexStatement getIndexStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFIndexStatement.class);
  }

  @Override
  @Nullable
  public LSFInternalStatement getInternalStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFInternalStatement.class);
  }

  @Override
  @Nullable
  public LSFLoggableStatement getLoggableStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFLoggableStatement.class);
  }

  @Override
  @Nullable
  public LSFMetaCodeDeclarationStatement getMetaCodeDeclarationStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFMetaCodeDeclarationStatement.class);
  }

  @Override
  @Nullable
  public LSFMetaCodeStatement getMetaCodeStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFMetaCodeStatement.class);
  }

  @Override
  @Nullable
  public LSFNavigatorStatement getNavigatorStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFNavigatorStatement.class);
  }

  @Override
  @Nullable
  public LSFOverrideActionStatement getOverrideActionStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFOverrideActionStatement.class);
  }

  @Override
  @Nullable
  public LSFOverridePropertyStatement getOverridePropertyStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFOverridePropertyStatement.class);
  }

  @Override
  @Nullable
  public LSFShowDepStatement getShowDepStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFShowDepStatement.class);
  }

  @Override
  @Nullable
  public LSFStubStatement getStubStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFStubStatement.class);
  }

  @Override
  @Nullable
  public LSFTableStatement getTableStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFTableStatement.class);
  }

  @Override
  @Nullable
  public LSFWindowStatement getWindowStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFWindowStatement.class);
  }

  @Override
  @Nullable
  public LSFWriteWhenStatement getWriteWhenStatement() {
    return PsiTreeUtil.getChildOfType(this, LSFWriteWhenStatement.class);
  }

}
