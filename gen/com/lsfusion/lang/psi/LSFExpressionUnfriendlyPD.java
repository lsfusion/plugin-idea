// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.openapi.util.Pair;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import java.util.Map;

public interface LSFExpressionUnfriendlyPD extends PsiElement {

  @Nullable
  LSFAbstractPropertyDefinition getAbstractPropertyDefinition();

  @Nullable
  LSFAggrPropertyDefinition getAggrPropertyDefinition();

  @Nullable
  LSFDataPropertyDefinition getDataPropertyDefinition();

  @Nullable
  LSFFilterPropertyDefinition getFilterPropertyDefinition();

  @Nullable
  LSFFormulaPropertyDefinition getFormulaPropertyDefinition();

  @Nullable
  LSFGroupPropertyDefinition getGroupPropertyDefinition();

  @Nullable
  LSFNativePropertyDefinition getNativePropertyDefinition();

  @Nullable
  LSFReflectionPropertyDefinition getReflectionPropertyDefinition();

  @Nullable LSFExClassSet resolveUnfriendValueClass(boolean infer);

  @Nullable List<LSFExClassSet> resolveValueParamClasses(List<LSFParamDeclaration> declareParams);

  @NotNull Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(List<LSFParamDeclaration> declareParams);

}
