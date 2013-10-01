package com.simpleplugin.psi.context;

import com.intellij.psi.PsiElement;
import com.simpleplugin.psi.LSFFormUsage;

public interface FormContext extends PsiElement {
    
    LSFFormUsage getFormUsage();
}
