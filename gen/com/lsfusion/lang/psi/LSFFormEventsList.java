// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.documentation.LSFDocumentation;

public interface LSFFormEventsList extends LSFDocumentation {

  @NotNull
  List<LSFFormEventDeclaration> getFormEventDeclarationList();

  String getDocumentation(PsiElement child);

}
