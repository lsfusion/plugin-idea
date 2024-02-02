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

public class LSFFormPropertyOptionsListImpl extends ASTWrapperPsiElement implements LSFFormPropertyOptionsList {

  public LSFFormPropertyOptionsListImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitFormPropertyOptionsList(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<LSFFormExtID> getFormExtIDList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormExtID.class);
  }

  @Override
  @NotNull
  public List<LSFFormInGroup> getFormInGroupList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormInGroup.class);
  }

  @Override
  @NotNull
  public List<LSFFormOptionColumns> getFormOptionColumnsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormOptionColumns.class);
  }

  @Override
  @NotNull
  public List<LSFFormOptionCustomView> getFormOptionCustomViewList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormOptionCustomView.class);
  }

  @Override
  @NotNull
  public List<LSFFormOptionEventId> getFormOptionEventIdList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormOptionEventId.class);
  }

  @Override
  @NotNull
  public List<LSFFormOptionForce> getFormOptionForceList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormOptionForce.class);
  }

  @Override
  @NotNull
  public List<LSFFormOptionInsertType> getFormOptionInsertTypeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormOptionInsertType.class);
  }

  @Override
  @NotNull
  public List<LSFFormOptionQuickFilter> getFormOptionQuickFilterList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormOptionQuickFilter.class);
  }

  @Override
  @NotNull
  public List<LSFFormOptionSession> getFormOptionSessionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormOptionSession.class);
  }

  @Override
  @NotNull
  public List<LSFFormOptionToDraw> getFormOptionToDrawList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormOptionToDraw.class);
  }

  @Override
  @NotNull
  public List<LSFFormOptionsOnEvents> getFormOptionsOnEventsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormOptionsOnEvents.class);
  }

  @Override
  @NotNull
  public List<LSFFormOptionsWithCalcPropertyObject> getFormOptionsWithCalcPropertyObjectList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormOptionsWithCalcPropertyObject.class);
  }

  @Override
  @NotNull
  public List<LSFFormOptionsWithOptionalCalcPropertyObject> getFormOptionsWithOptionalCalcPropertyObjectList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFormOptionsWithOptionalCalcPropertyObject.class);
  }

  @Override
  @NotNull
  public List<LSFStickyOption> getStickyOptionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFStickyOption.class);
  }

  @Override
  @NotNull
  public List<LSFSyncTypeLiteral> getSyncTypeLiteralList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFSyncTypeLiteral.class);
  }

}
