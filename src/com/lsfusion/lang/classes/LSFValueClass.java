package com.lsfusion.lang.classes;

import com.intellij.psi.PsiElement;

import java.util.List;

public interface LSFValueClass {
    String getName();

    String getQName(PsiElement context);

    String getCaption();
    
    List<String> getSNames();
    
    LSFClassSet getUpSet();

    boolean isValid();
}
