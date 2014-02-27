package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import org.jetbrains.annotations.Nullable;

public interface FormContext extends PsiElement {
    @Nullable
    LSFFormDeclaration resolveFormDecl();
}
