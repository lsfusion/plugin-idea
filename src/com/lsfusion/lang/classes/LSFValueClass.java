package com.lsfusion.lang.classes;

import com.intellij.psi.PsiElement;

import java.util.List;

public interface LSFValueClass {
    public String getName();

    public String getQName(PsiElement context);

    String getCaption();

    List<String> getSNames();
}
