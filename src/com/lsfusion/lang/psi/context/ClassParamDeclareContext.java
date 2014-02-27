package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.meta.MetaTransaction;
import org.jetbrains.annotations.NotNull;

public interface ClassParamDeclareContext extends PsiElement {
    
    LSFClassSet resolveClass();
    
    void ensureClass(@NotNull LSFValueClass valueClass, MetaTransaction metaTrans);
}
