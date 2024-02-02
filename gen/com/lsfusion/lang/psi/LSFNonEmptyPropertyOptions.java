// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.documentation.LSFDocumentation;

public interface LSFNonEmptyPropertyOptions extends LSFDocumentation {

  @NotNull
  List<LSFAggrSetting> getAggrSettingList();

  @NotNull
  List<LSFAsEditActionSetting> getAsEditActionSettingList();

  @NotNull
  List<LSFAutosetSetting> getAutosetSettingList();

  @NotNull
  List<LSFChangeKeySetting> getChangeKeySettingList();

  @NotNull
  List<LSFChangeMouseSetting> getChangeMouseSettingList();

  @NotNull
  List<LSFCharWidthSetting> getCharWidthSettingList();

  @NotNull
  List<LSFComplexSetting> getComplexSettingList();

  @NotNull
  List<LSFConfirmSetting> getConfirmSettingList();

  @NotNull
  List<LSFCustomViewSetting> getCustomViewSettingList();

  @NotNull
  List<LSFDefaultCompareSetting> getDefaultCompareSettingList();

  @NotNull
  List<LSFEchoSymbolsSetting> getEchoSymbolsSettingList();

  @NotNull
  List<LSFEventIdSetting> getEventIdSettingList();

  @NotNull
  List<LSFFlexCharWidthSetting> getFlexCharWidthSettingList();

  @NotNull
  List<LSFGroupUsage> getGroupUsageList();

  @NotNull
  List<LSFHintSetting> getHintSettingList();

  @NotNull
  List<LSFImageSetting> getImageSettingList();

  @NotNull
  List<LSFIndexSetting> getIndexSettingList();

  @NotNull
  List<LSFLoggableSetting> getLoggableSettingList();

  @NotNull
  List<LSFNotNullSetting> getNotNullSettingList();

  @NotNull
  List<LSFOnEditEventSetting> getOnEditEventSettingList();

  @NotNull
  List<LSFPatternSetting> getPatternSettingList();

  @NotNull
  List<LSFPersistentSetting> getPersistentSettingList();

  @NotNull
  List<LSFPrereadSetting> getPrereadSettingList();

  @NotNull
  List<LSFRegexpSetting> getRegexpSettingList();

  @NotNull
  List<LSFSimpleName> getSimpleNameList();

  @NotNull
  List<LSFStickyOption> getStickyOptionList();

  @NotNull
  List<LSFSyncTypeLiteral> getSyncTypeLiteralList();

  @NotNull
  List<LSFTableUsage> getTableUsageList();

  @NotNull
  List<LSFViewTypeSetting> getViewTypeSettingList();

  String getDocumentation(PsiElement child);

}
