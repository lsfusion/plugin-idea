package com.lsfusion.lang.psi;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.meta.MetaTransaction;

public interface LSFId extends PsiElement {

    String getName();
    PsiElement setName(String name);
    void setName(String newName, MetaTransaction transaction);
}
