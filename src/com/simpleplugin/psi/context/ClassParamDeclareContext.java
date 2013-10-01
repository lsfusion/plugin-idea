package com.simpleplugin.psi.context;

import com.intellij.psi.PsiElement;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.classes.LSFValueClass;
import com.simpleplugin.meta.MetaTransaction;
import org.jetbrains.annotations.NotNull;

public interface ClassParamDeclareContext extends PsiElement {
    
    LSFClassSet resolveClass();
    
    void ensureClass(@NotNull LSFValueClass valueClass, MetaTransaction metaTrans);
}
