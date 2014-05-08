package com.lsfusion.lang.psi.references;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.LSFStringLiteral;
import org.jetbrains.annotations.NotNull;

public interface LSFJavaClassStringReference extends PsiElement {
    
    @NotNull
    LSFStringLiteral getStringLiteral();

    void setNewText(String newText);
}
