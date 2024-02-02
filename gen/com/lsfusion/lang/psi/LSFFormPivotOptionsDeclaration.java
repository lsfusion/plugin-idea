// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.documentation.LSFDocumentation;

public interface LSFFormPivotOptionsDeclaration extends LSFDocumentation {

  @NotNull
  List<LSFFormPropertyDrawUsage> getFormPropertyDrawUsageList();

  @NotNull
  List<LSFGroupObjectUsage> getGroupObjectUsageList();

  @NotNull
  List<LSFPivotOptions> getPivotOptionsList();

  @NotNull
  List<LSFPivotPropertyDrawList> getPivotPropertyDrawListList();

  String getDocumentation(PsiElement child);

}
