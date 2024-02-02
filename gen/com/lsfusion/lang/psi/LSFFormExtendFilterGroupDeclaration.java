// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.documentation.LSFDocumentation;

public interface LSFFormExtendFilterGroupDeclaration extends LSFDocumentation {

  @Nullable
  LSFFilterGroupUsage getFilterGroupUsage();

  @NotNull
  List<LSFRegularFilterDeclaration> getRegularFilterDeclarationList();

  String getDocumentation(PsiElement child);

}
