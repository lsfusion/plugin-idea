// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFObjectDeclaration;

public interface LSFFormObjectDeclaration extends LSFObjectDeclaration {

  @NotNull
  LSFClassName getClassName();

  @Nullable
  LSFFormActionPropertyObject getFormActionPropertyObject();

  @Nullable
  LSFLocalizedStringLiteral getLocalizedStringLiteral();

  @Nullable
  LSFSimpleName getSimpleName();

}
