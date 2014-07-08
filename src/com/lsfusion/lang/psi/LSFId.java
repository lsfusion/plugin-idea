package com.lsfusion.lang.psi;

import com.intellij.psi.PsiNamedElement;
import com.lsfusion.lang.meta.MetaTransaction;

public interface LSFId extends PsiNamedElement {

    void setName(String newName, MetaTransaction transaction);
}
