package com.lsfusion.lang.psi;

import com.intellij.psi.PsiElement;

public interface LSFElement extends PsiElement {

    boolean isCorrect(); // для pin'ов, если pin сработал а не все элементы есть
    
    LSFFile getLSFFile();
}
