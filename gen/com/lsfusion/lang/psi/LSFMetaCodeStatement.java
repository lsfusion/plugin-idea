// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.references.LSFMetaReference;

public interface LSFMetaCodeStatement extends LSFMetaReference {

  @Nullable
  LSFMetaCodeBody getMetaCodeBody();

  @NotNull
  LSFMetaCodeStatementHeader getMetaCodeStatementHeader();

  @Nullable
  LSFMetaCodeStatementSemi getMetaCodeStatementSemi();

  boolean isInline();

}
