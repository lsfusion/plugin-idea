package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;

import java.util.List;

public interface UnfriendlyPE extends PsiElement {

    LSFClassSet resolveUnfriendValueClass(boolean infer);

    List<LSFClassSet> resolveValueParamClasses();

    List<String> getValueParamClassNames();

    List<String> getValueClassNames();

    List<String> getValuePropertyNames();
}
