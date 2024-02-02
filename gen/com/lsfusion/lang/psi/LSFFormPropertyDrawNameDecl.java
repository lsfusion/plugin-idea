// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawNameDeclaration;

public interface LSFFormPropertyDrawNameDecl extends LSFPropertyDrawNameDeclaration {

  @NotNull
  LSFFormPropertyName getFormPropertyName();

  @NotNull
  LSFFormPropertyOptionsList getFormPropertyOptionsList();

  @Nullable
  LSFLocalizedStringLiteral getLocalizedStringLiteral();

  @Nullable
  LSFSimpleName getSimpleName();

}
