// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.MetaStubElement;

public interface LSFMetaCodeDeclarationStatement extends LSFMetaDeclaration, StubBasedPsiElement<MetaStubElement> {

  @Nullable
  LSFMetaCodeDeclBody getMetaCodeDeclBody();

  @Nullable
  LSFMetaDeclIdList getMetaDeclIdList();

  @NotNull
  LSFSimpleName getSimpleName();

  String getDocumentation(PsiElement child);

}
