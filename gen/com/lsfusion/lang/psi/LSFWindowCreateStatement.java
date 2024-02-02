// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.ModifyParamContext;
import com.lsfusion.lang.psi.declarations.LSFWindowDeclaration;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.WindowStubElement;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;

public interface LSFWindowCreateStatement extends ModifyParamContext, LSFWindowDeclaration, StubBasedPsiElement<WindowStubElement> {

  @Nullable
  LSFLocalizedStringLiteral getLocalizedStringLiteral();

  @NotNull
  LSFSimpleName getSimpleName();

  @Nullable
  LSFWindowOptions getWindowOptions();

  @Nullable
  LSFWindowType getWindowType();

  ContextModifier getContextModifier();

  ContextInferrer getContextInferrer();

  String getDocumentation(PsiElement child);

}
