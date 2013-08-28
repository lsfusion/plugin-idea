package com.simpleplugin.psi.context;

import com.intellij.psi.PsiElement;
import com.simpleplugin.classes.LSFClassSet;

public interface ClassParamDeclareContext extends PsiElement {
    
    LSFClassSet resolveClass();
}
