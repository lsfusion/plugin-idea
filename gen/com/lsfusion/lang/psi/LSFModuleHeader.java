// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.ModuleStubElement;

public interface LSFModuleHeader extends LSFModuleDeclaration, StubBasedPsiElement<ModuleStubElement> {

  @NotNull
  LSFModuleNameStatement getModuleNameStatement();

  @Nullable
  LSFNamespaceName getNamespaceName();

  @Nullable
  LSFPriorityList getPriorityList();

  @Nullable
  LSFRequireList getRequireList();

  String getDocumentation(PsiElement child);

}
