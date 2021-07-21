package com.lsfusion.documentation;

import com.intellij.psi.PsiElement;

public interface LSFDocumentation extends PsiElement {

    String getDocumentation(PsiElement child);

}
