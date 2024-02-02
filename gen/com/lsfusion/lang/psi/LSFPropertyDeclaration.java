// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;

public interface LSFPropertyDeclaration extends PsiElement {

  @Nullable
  LSFPropertyDeclParams getPropertyDeclParams();

  @NotNull
  LSFSimpleNameWithCaption getSimpleNameWithCaption();

  @Nullable List<LSFParamDeclaration> resolveParamDecls();

}
