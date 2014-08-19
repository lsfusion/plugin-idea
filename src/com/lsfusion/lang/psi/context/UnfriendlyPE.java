package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.typeinfer.LSFExClassSet;

import java.util.List;

public interface UnfriendlyPE extends PsiElement {

    LSFExClassSet resolveUnfriendValueClass(boolean infer);

    List<LSFExClassSet> resolveValueParamClasses();

    List<String> getValueParamClassNames();

    List<String> getValueClassNames();

    List<String> getValuePropertyNames();
}
