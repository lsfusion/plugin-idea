// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.documentation.LSFDocumentation;

public interface LSFLocalDataPropertyDefinition extends LSFDocumentation {

  @Nullable
  LSFClassName getClassName();

  @Nullable
  LSFClassNameList getClassNameList();

  @Nullable
  LSFNestedLocalModifier getNestedLocalModifier();

  @Nullable
  LSFNonEmptyLocalPropertyDeclarationNameList getNonEmptyLocalPropertyDeclarationNameList();

  String getDocumentation(PsiElement child);

}
