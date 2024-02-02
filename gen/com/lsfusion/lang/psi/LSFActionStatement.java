// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFStatementActionDeclaration;
import com.lsfusion.lang.psi.context.ModifyParamContext;
import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.stubs.StatementActionStubElement;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;

public interface LSFActionStatement extends LSFStatementActionDeclaration, ModifyParamContext, LSFInterfacePropStatement, StubBasedPsiElement<StatementActionStubElement> {

  @Nullable
  LSFActionUnfriendlyPD getActionUnfriendlyPD();

  @Nullable
  LSFListActionPropertyDefinitionBody getListActionPropertyDefinitionBody();

  @Nullable
  LSFNonEmptyPropertyOptions getNonEmptyPropertyOptions();

  @NotNull
  LSFPropertyDeclaration getPropertyDeclaration();

  ContextModifier getContextModifier();

  ContextInferrer getContextInferrer();

  String getDocumentation(PsiElement child);

}
