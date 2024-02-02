// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.lsfusion.lang.psi.LSFTypes.*;
import com.lsfusion.lang.psi.LSFLazyParsableElement;
import com.lsfusion.lang.psi.*;
import com.intellij.psi.tree.IElementType;

public class LSFLazyMetaDeclStatementImpl extends LSFLazyParsableElement implements LSFLazyMetaDeclStatement {

  public LSFLazyMetaDeclStatementImpl(ASTNode node) {
    super(node);
  }

  public LSFLazyMetaDeclStatementImpl(@NotNull IElementType elementType, CharSequence buffer) {
    super(elementType, buffer);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitLazyMetaDeclStatement(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<LSFAspectStatement> getAspectStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFAspectStatement.class);
  }

  @Override
  @NotNull
  public List<LSFClassStatement> getClassStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFClassStatement.class);
  }

  @Override
  @NotNull
  public List<LSFConstraintStatement> getConstraintStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFConstraintStatement.class);
  }

  @Override
  @NotNull
  public List<LSFDesignStatement> getDesignStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFDesignStatement.class);
  }

  @Override
  @NotNull
  public List<LSFEmptyStatement> getEmptyStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFEmptyStatement.class);
  }

  @Override
  @NotNull
  public List<LSFEventStatement> getEventStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFEventStatement.class);
  }

  @Override
  @NotNull
  public List<LSFExplicitInterfaceActStatement> getExplicitInterfaceActStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFExplicitInterfaceActStatement.class);
  }

  @Override
  @NotNull
  public List<LSFExplicitInterfacePropertyStatement> getExplicitInterfacePropertyStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFExplicitInterfacePropertyStatement.class);
  }

  @Override
  @NotNull
  public List<LSFFollowsStatement> getFollowsStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFollowsStatement.class);
  }

  @Override
  @NotNull
  public List<LSFFormStatement> getFormStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormStatement.class);
  }

  @Override
  @NotNull
  public List<LSFGlobalEventStatement> getGlobalEventStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFGlobalEventStatement.class);
  }

  @Override
  @NotNull
  public List<LSFGroupStatement> getGroupStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFGroupStatement.class);
  }

  @Override
  @NotNull
  public List<LSFIndexStatement> getIndexStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFIndexStatement.class);
  }

  @Override
  @NotNull
  public List<LSFInternalStatement> getInternalStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFInternalStatement.class);
  }

  @Override
  @NotNull
  public List<LSFLoggableStatement> getLoggableStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFLoggableStatement.class);
  }

  @Override
  @NotNull
  public List<LSFMetaCodeStatement> getMetaCodeStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFMetaCodeStatement.class);
  }

  @Override
  @NotNull
  public List<LSFNavigatorStatement> getNavigatorStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFNavigatorStatement.class);
  }

  @Override
  @NotNull
  public List<LSFOverrideActionStatement> getOverrideActionStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFOverrideActionStatement.class);
  }

  @Override
  @NotNull
  public List<LSFOverridePropertyStatement> getOverridePropertyStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFOverridePropertyStatement.class);
  }

  @Override
  @NotNull
  public List<LSFShowDepStatement> getShowDepStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFShowDepStatement.class);
  }

  @Override
  @NotNull
  public List<LSFStubStatement> getStubStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFStubStatement.class);
  }

  @Override
  @NotNull
  public List<LSFTableStatement> getTableStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFTableStatement.class);
  }

  @Override
  @NotNull
  public List<LSFWindowStatement> getWindowStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFWindowStatement.class);
  }

  @Override
  @NotNull
  public List<LSFWriteWhenStatement> getWriteWhenStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFWriteWhenStatement.class);
  }

}
