// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFNavigatorElementDeclaration;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.NavigatorElementStubElement;

public interface LSFNewNavigatorElementStatement extends LSFNavigatorElementDeclaration, StubBasedPsiElement<NavigatorElementStubElement> {

  @NotNull
  LSFNavigatorElementDescription getNavigatorElementDescription();

  @Nullable
  LSFNavigatorElementOptions getNavigatorElementOptions();

  @Nullable
  LSFNavigatorElementStatementBody getNavigatorElementStatementBody();

}
