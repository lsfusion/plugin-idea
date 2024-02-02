// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.documentation.LSFDocumentation;

public interface LSFObjectInputProps extends LSFDocumentation {

  @Nullable
  LSFChangeInput getChangeInput();

  @Nullable
  LSFConstraintFilter getConstraintFilter();

  @Nullable
  LSFObjectListInputProps getObjectListInputProps();

  @Nullable
  LSFSimpleName getSimpleName();

  @Nullable
  LSFStaticDestination getStaticDestination();

  String getDocumentation(PsiElement child);

}
