// This is a generated file. Not intended for manual editing.
package com.lsfusion.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.context.UnfriendlyPE;
import com.intellij.openapi.util.Pair;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import java.util.Map;

public interface LSFFilterPropertyDefinition extends UnfriendlyPE {

  @Nullable
  LSFGroupObjectID getGroupObjectID();

  @Nullable LSFExClassSet resolveUnfriendValueClass(boolean infer);

  @Nullable List<LSFExClassSet> resolveValueParamClasses(List<LSFParamDeclaration> declareParams);

  @NotNull Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(PsiElement singleElement, List<LSFParamDeclaration> declareParams);

  @NotNull Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(LSFClassNameList classNameList, List<LSFParamDeclaration> declareParams);

  @NotNull Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(List<? extends PsiElement> elements, List<LSFParamDeclaration> declareParams);

  @NotNull Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(List<LSFParamDeclaration> declareParams);

  LSFExplicitClasses getValueParamClassNames();

  List<String> getValueClassNames();

  List<String> getValuePropertyNames();

  String getDocumentation(PsiElement child);

}
