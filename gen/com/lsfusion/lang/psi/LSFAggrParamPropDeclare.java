// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFAggrParamGlobalPropDeclaration;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.AggrParamPropStubElement;

public interface LSFAggrParamPropDeclare extends LSFAggrParamGlobalPropDeclaration, StubBasedPsiElement<AggrParamPropStubElement> {

  @NotNull
  LSFClassName getClassName();

  @NotNull
  LSFParamDeclare getParamDeclare();

}
