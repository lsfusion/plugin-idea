// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.ClassParamDeclareContext;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.meta.MetaTransaction;

public interface LSFClassParamDeclare extends ClassParamDeclareContext {

  @Nullable
  LSFAggrParamPropDeclare getAggrParamPropDeclare();

  @Nullable
  LSFUntypedParamDeclare getUntypedParamDeclare();

  @Nullable LSFClassSet resolveClass();

  void ensureClass(@NotNull LSFValueClass valueClass, MetaTransaction metaTrans);

  LSFClassName getClassName();

  @NotNull LSFParamDeclare getParamDeclare();

}
