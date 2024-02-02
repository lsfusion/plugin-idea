// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.ClassStubElement;

public interface LSFClassDecl extends LSFClassDeclaration, StubBasedPsiElement<ClassStubElement> {

  @Nullable
  LSFSimpleNameWithCaption getSimpleNameWithCaption();

  @Nullable
  LSFStaticObjectImage getStaticObjectImage();

  String getDocumentation(PsiElement child);

}
