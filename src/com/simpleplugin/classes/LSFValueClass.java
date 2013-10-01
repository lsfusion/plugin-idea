package com.simpleplugin.classes;

import com.intellij.psi.PsiElement;
import com.simpleplugin.psi.LSFElement;

public interface LSFValueClass {
    
    public String getQName(PsiElement context);
}
