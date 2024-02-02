// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.documentation.LSFDocumentation;

public interface LSFFormGroupObjectOptions extends LSFDocumentation {

  @NotNull
  List<LSFFormExtID> getFormExtIDList();

  @NotNull
  List<LSFFormExtKey> getFormExtKeyList();

  @NotNull
  List<LSFFormGroupObjectBackground> getFormGroupObjectBackgroundList();

  @NotNull
  List<LSFFormGroupObjectForeground> getFormGroupObjectForegroundList();

  @NotNull
  List<LSFFormGroupObjectInitViewType> getFormGroupObjectInitViewTypeList();

  @NotNull
  List<LSFFormGroupObjectPageSize> getFormGroupObjectPageSizeList();

  @NotNull
  List<LSFFormGroupObjectRelativePosition> getFormGroupObjectRelativePositionList();

  @NotNull
  List<LSFFormGroupObjectUpdate> getFormGroupObjectUpdateList();

  @NotNull
  List<LSFFormGroupObjectViewType> getFormGroupObjectViewTypeList();

  @NotNull
  List<LSFFormInGroup> getFormInGroupList();

  @NotNull
  List<LSFFormSubReport> getFormSubReportList();

  String getDocumentation(PsiElement child);

}
