// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFStatementGlobalPropDeclaration;
import com.lsfusion.lang.psi.context.ModifyParamContext;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.StatementPropStubElement;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;

public interface LSFPropertyStatement extends LSFStatementGlobalPropDeclaration, ModifyParamContext, LSFInterfacePropStatement, StubBasedPsiElement<StatementPropStubElement> {

  @NotNull
  LSFEqualsSign getEqualsSign();

  @Nullable
  LSFNonEmptyPropertyOptions getNonEmptyPropertyOptions();

  @NotNull
  LSFPropertyCalcStatement getPropertyCalcStatement();

  @NotNull
  LSFPropertyDeclaration getPropertyDeclaration();

  ContextModifier getContextModifier();

  ContextInferrer getContextInferrer();

  String getDocumentation(PsiElement child);

}
