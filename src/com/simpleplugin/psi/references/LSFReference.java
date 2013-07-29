package com.simpleplugin.psi.references;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;

public interface LSFReference extends PsiReference, PsiElement {

    LSFId getSimpleName(); // getSimpleName чтобы по умолчанию подтянуть реализации
}
