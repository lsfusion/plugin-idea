package com.lsfusion.documentation;

import com.intellij.psi.PsiElement;

public interface LSFDocumentation extends PsiElement {

    default String getDocumentation(PsiElement child) {
        return null;
    }

}
