package com.lsfusion.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.classes.LSFClassSet;
import com.lsfusion.classes.LSFValueClass;
import com.lsfusion.meta.MetaTransaction;
import org.jetbrains.annotations.NotNull;

public interface ClassParamDeclareContext extends PsiElement {
    
    LSFClassSet resolveClass();
    
    void ensureClass(@NotNull LSFValueClass valueClass, MetaTransaction metaTrans);
}
