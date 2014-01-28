package com.simpleplugin.classes;

import com.intellij.psi.PsiElement;

public interface LSFValueClass {
    public String getName();

    public String getQName(PsiElement context);
}
