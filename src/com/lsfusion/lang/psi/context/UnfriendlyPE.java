package com.lsfusion.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.classes.LSFClassSet;

import java.util.List;

public interface UnfriendlyPE extends PsiElement {

    LSFClassSet resolveUnfriendValueClass(boolean infer);

    List<LSFClassSet> resolveValueParamClasses();

    List<String> getValueParamClassNames();

    List<String> getValueClassNames();

    List<String> getValuePropertyNames();
}
