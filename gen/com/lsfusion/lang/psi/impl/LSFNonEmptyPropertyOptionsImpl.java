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

public class LSFNonEmptyPropertyOptionsImpl extends ASTWrapperPsiElement implements LSFNonEmptyPropertyOptions {

  public LSFNonEmptyPropertyOptionsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull LSFVisitor visitor) {
    visitor.visitNonEmptyPropertyOptions(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LSFVisitor) accept((LSFVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<LSFAggrSetting> getAggrSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFAggrSetting.class);
  }

  @Override
  @NotNull
  public List<LSFAsEditActionSetting> getAsEditActionSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFAsEditActionSetting.class);
  }

  @Override
  @NotNull
  public List<LSFAutosetSetting> getAutosetSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFAutosetSetting.class);
  }

  @Override
  @NotNull
  public List<LSFChangeKeySetting> getChangeKeySettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFChangeKeySetting.class);
  }

  @Override
  @NotNull
  public List<LSFChangeMouseSetting> getChangeMouseSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFChangeMouseSetting.class);
  }

  @Override
  @NotNull
  public List<LSFCharWidthSetting> getCharWidthSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFCharWidthSetting.class);
  }

  @Override
  @NotNull
  public List<LSFComplexSetting> getComplexSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFComplexSetting.class);
  }

  @Override
  @NotNull
  public List<LSFConfirmSetting> getConfirmSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFConfirmSetting.class);
  }

  @Override
  @NotNull
  public List<LSFCustomViewSetting> getCustomViewSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFCustomViewSetting.class);
  }

  @Override
  @NotNull
  public List<LSFDefaultCompareSetting> getDefaultCompareSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFDefaultCompareSetting.class);
  }

  @Override
  @NotNull
  public List<LSFEchoSymbolsSetting> getEchoSymbolsSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFEchoSymbolsSetting.class);
  }

  @Override
  @NotNull
  public List<LSFEventIdSetting> getEventIdSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFEventIdSetting.class);
  }

  @Override
  @NotNull
  public List<LSFFlexCharWidthSetting> getFlexCharWidthSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFFlexCharWidthSetting.class);
  }

  @Override
  @NotNull
  public List<LSFGroupUsage> getGroupUsageList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFGroupUsage.class);
  }

  @Override
  @NotNull
  public List<LSFHintSetting> getHintSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFHintSetting.class);
  }

  @Override
  @NotNull
  public List<LSFImageSetting> getImageSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFImageSetting.class);
  }

  @Override
  @NotNull
  public List<LSFIndexSetting> getIndexSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFIndexSetting.class);
  }

  @Override
  @NotNull
  public List<LSFLoggableSetting> getLoggableSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFLoggableSetting.class);
  }

  @Override
  @NotNull
  public List<LSFNotNullSetting> getNotNullSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFNotNullSetting.class);
  }

  @Override
  @NotNull
  public List<LSFOnEditEventSetting> getOnEditEventSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFOnEditEventSetting.class);
  }

  @Override
  @NotNull
  public List<LSFPatternSetting> getPatternSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFPatternSetting.class);
  }

  @Override
  @NotNull
  public List<LSFPersistentSetting> getPersistentSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFPersistentSetting.class);
  }

  @Override
  @NotNull
  public List<LSFPrereadSetting> getPrereadSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFPrereadSetting.class);
  }

  @Override
  @NotNull
  public List<LSFRegexpSetting> getRegexpSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFRegexpSetting.class);
  }

  @Override
  @NotNull
  public List<LSFSimpleName> getSimpleNameList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFSimpleName.class);
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

  @Override
  @NotNull
  public List<LSFTableUsage> getTableUsageList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFTableUsage.class);
  }

  @Override
  @NotNull
  public List<LSFViewTypeSetting> getViewTypeSettingList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, LSFViewTypeSetting.class);
  }

  @Override
  public String getDocumentation(PsiElement child) {
    return LSFPsiImplUtil.getDocumentation(this, child);
  }

}
