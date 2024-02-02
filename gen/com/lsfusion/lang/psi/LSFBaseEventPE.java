// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFBaseEventActionDeclaration;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.BaseEventActionStubElement;

public interface LSFBaseEventPE extends LSFBaseEventActionDeclaration, StubBasedPsiElement<BaseEventActionStubElement> {

  @NotNull
  LSFBaseEvent getBaseEvent();

  @Nullable
  LSFParamDeclare getParamDeclare();

}
