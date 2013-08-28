package com.simpleplugin.psi.context;

import com.intellij.psi.PsiElement;
import com.simpleplugin.classes.LSFClassSet;

import java.util.List;

public interface PropertyUsageContext extends PsiElement {
    
    List<LSFClassSet> resolveParamClasses();
}
