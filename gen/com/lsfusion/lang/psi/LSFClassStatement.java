// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.extend.ExtendClassStubElement;

public interface LSFClassStatement extends LSFClassExtend, StubBasedPsiElement<ExtendClassStubElement> {

  @Nullable
  LSFClassDecl getClassDecl();

  @Nullable
  LSFClassParentsList getClassParentsList();

  @Nullable
  LSFExtendingClassDeclaration getExtendingClassDeclaration();

  @Nullable
  LSFStaticObjectDeclList getStaticObjectDeclList();

  String getDocumentation(PsiElement child);

}
