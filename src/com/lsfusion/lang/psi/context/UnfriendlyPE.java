package com.lsfusion.lang.psi.context;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;
import com.lsfusion.lang.typeinfer.LSFExClassSet;

import java.util.List;
import java.util.Map;

public interface UnfriendlyPE extends PsiElement {

    // только у property
    LSFExClassSet resolveUnfriendValueClass(boolean infer);

    List<String> getValueClassNames();

    List<String> getValuePropertyNames();

    // и у property и у action'ов

    List<LSFExClassSet> resolveValueParamClasses(List<LSFParamDeclaration> declareParams);

    Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(List<LSFParamDeclaration> declareParams);

    LSFExplicitClasses getValueParamClassNames();

}
