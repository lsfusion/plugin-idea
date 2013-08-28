package com.simpleplugin.psi.context;

import com.intellij.psi.PsiElement;
import com.simpleplugin.classes.LSFClassSet;

public interface PropExpression extends PsiElement {
    
    LSFClassSet resolveValueClass();
}
