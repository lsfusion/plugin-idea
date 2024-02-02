// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.documentation.LSFDocumentation;

public interface LSFFormPropertiesList extends LSFDocumentation {

  @Nullable
  LSFFormMappedNamePropertiesList getFormMappedNamePropertiesList();

  @Nullable
  LSFFormMappedPropertiesList getFormMappedPropertiesList();

  @Nullable
  LSFFormPropertyOptionsList getFormPropertyOptionsList();

  String getDocumentation(PsiElement child);

}
