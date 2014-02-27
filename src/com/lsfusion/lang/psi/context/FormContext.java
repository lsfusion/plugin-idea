package com.lsfusion.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.psi.declarations.LSFFormDeclaration;
import org.jetbrains.annotations.Nullable;

public interface FormContext extends PsiElement {
    @Nullable
    LSFFormDeclaration resolveFormDecl();
}
