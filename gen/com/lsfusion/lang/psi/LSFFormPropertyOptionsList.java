// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface LSFFormPropertyOptionsList extends PsiElement {

  @NotNull
  List<LSFFormExtID> getFormExtIDList();

  @NotNull
  List<LSFFormInGroup> getFormInGroupList();

  @NotNull
  List<LSFFormOptionColumns> getFormOptionColumnsList();

  @NotNull
  List<LSFFormOptionCustomView> getFormOptionCustomViewList();

  @NotNull
  List<LSFFormOptionEventId> getFormOptionEventIdList();

  @NotNull
  List<LSFFormOptionForce> getFormOptionForceList();

  @NotNull
  List<LSFFormOptionInsertType> getFormOptionInsertTypeList();

  @NotNull
  List<LSFFormOptionQuickFilter> getFormOptionQuickFilterList();

  @NotNull
  List<LSFFormOptionSession> getFormOptionSessionList();

  @NotNull
  List<LSFFormOptionToDraw> getFormOptionToDrawList();

  @NotNull
  List<LSFFormOptionsOnEvents> getFormOptionsOnEventsList();

  @NotNull
  List<LSFFormOptionsWithCalcPropertyObject> getFormOptionsWithCalcPropertyObjectList();

  @NotNull
  List<LSFFormOptionsWithOptionalCalcPropertyObject> getFormOptionsWithOptionalCalcPropertyObjectList();

  @NotNull
  List<LSFStickyOption> getStickyOptionList();

  @NotNull
  List<LSFSyncTypeLiteral> getSyncTypeLiteralList();

}
