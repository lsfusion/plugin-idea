package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;
import com.lsfusion.lang.typeinfer.LSFExClassSet;

import java.util.List;

public interface UnfriendlyPE extends PsiElement {

    // только у property
    LSFExClassSet resolveUnfriendValueClass(boolean infer);

    List<String> getValueClassNames();

    List<String> getValuePropertyNames();

    // и у property и у action'ов

    List<LSFExClassSet> resolveValueParamClasses(List<LSFParamDeclaration> declareParams);

    LSFExplicitClasses getValueParamClassNames();

}
