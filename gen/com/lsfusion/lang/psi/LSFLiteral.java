// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.documentation.LSFDocumentation;

public interface LSFLiteral extends LSFDocumentation {

  @Nullable
  LSFBooleanLiteral getBooleanLiteral();

  @Nullable
  LSFColorLiteral getColorLiteral();

  @Nullable
  LSFDateLiteral getDateLiteral();

  @Nullable
  LSFDateTimeLiteral getDateTimeLiteral();

  @Nullable
  LSFLocalizedStringLiteral getLocalizedStringLiteral();

  @Nullable
  LSFNullLiteral getNullLiteral();

  @Nullable
  LSFStaticObjectID getStaticObjectID();

  @Nullable
  LSFTbooleanLiteral getTbooleanLiteral();

  @Nullable
  LSFTimeLiteral getTimeLiteral();

  @Nullable
  LSFUdoubleLiteral getUdoubleLiteral();

  @Nullable
  LSFUintLiteral getUintLiteral();

  @Nullable
  LSFUlongLiteral getUlongLiteral();

  @Nullable
  LSFUnumericLiteral getUnumericLiteral();

  String getDocumentation(PsiElement child);

}
