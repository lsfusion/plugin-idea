// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.PropertyUsageContext;
import com.lsfusion.lang.classes.LSFClassSet;

public interface LSFMappedActionClassParamDeclare extends PropertyUsageContext {

  @NotNull
  LSFActionUsageWrapper getActionUsageWrapper();

  @Nullable
  LSFClassParamDeclareList getClassParamDeclareList();

  @Nullable List<LSFClassSet> resolveParamClasses();

  @Nullable PsiElement getParamList();

}
