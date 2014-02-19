package com.simpleplugin.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;

public interface LSFElement extends PsiElement {

    boolean isCorrect(); // для pin'ов, если pin сработал а не все элементы есть
    
    LSFFile getLSFFile();

    GlobalSearchScope getScope();
}
