// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.ModifyParamContext;
import com.lsfusion.lang.psi.context.ContextInferrer;
import com.lsfusion.lang.psi.context.ContextModifier;
import javax.swing.Icon;

public interface LSFOverrideActionStatement extends ModifyParamContext {

  @NotNull
  LSFListActionPropertyDefinitionBody getListActionPropertyDefinitionBody();

  @NotNull
  LSFMappedActionClassParamDeclare getMappedActionClassParamDeclare();

  @Nullable
  LSFPropertyExpression getPropertyExpression();

  ContextModifier getContextModifier();

  ContextInferrer getContextInferrer();

  @Nullable Icon getIcon(int flags);

  String getDocumentation(PsiElement child);

}
