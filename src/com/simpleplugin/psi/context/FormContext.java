package com.simpleplugin.psi.context;

import com.intellij.psi.PsiElement;
import com.simpleplugin.psi.declarations.LSFFormDeclaration;
import org.jetbrains.annotations.Nullable;

public interface FormContext extends PsiElement {
    @Nullable
    LSFFormDeclaration resolveFormDecl();
}
