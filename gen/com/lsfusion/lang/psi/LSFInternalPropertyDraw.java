// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.FormContext;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;

public interface LSFInternalPropertyDraw extends FormContext {

  @Nullable
  LSFFormPropertyDrawUsage getFormPropertyDrawUsage();

  @Nullable
  LSFFormUsage getFormUsage();

  @Nullable LSFFormDeclaration resolveFormDecl();

}
