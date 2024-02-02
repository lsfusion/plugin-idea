// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface LSFMetaCodeBody extends PsiElement {

  @NotNull
  List<LSFLazyMetaStatement> getLazyMetaStatementList();

  @NotNull
  LSFMetaCodeBodyLeftBrace getMetaCodeBodyLeftBrace();

  @NotNull
  LSFMetaCodeBodyRightBrace getMetaCodeBodyRightBrace();

}
