package com.simpleplugin.psi.context;

import com.intellij.psi.PsiElement;
import com.simpleplugin.classes.LSFClassSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PropertyUsageContext extends PsiElement {
    
    @Nullable
    List<LSFClassSet> resolveParamClasses();
}
