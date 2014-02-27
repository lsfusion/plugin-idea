package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PropertyUsageContext extends PsiElement {
    
    @Nullable
    List<LSFClassSet> resolveParamClasses();
    
    @Nullable
    PsiElement getParamList();
}
