// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.TableStubElement;

public interface LSFTableStatement extends LSFTableDeclaration, StubBasedPsiElement<TableStubElement> {

  @Nullable
  LSFClassNameList getClassNameList();

  @Nullable
  LSFNoDefault getNoDefault();

  @NotNull
  LSFSimpleName getSimpleName();

  @Nullable
  LSFStringLiteral getStringLiteral();

  String getDocumentation(PsiElement child);

}
