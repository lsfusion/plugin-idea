package com.simpleplugin.psi.references;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.simpleplugin.psi.LSFElement;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;

public interface LSFReference extends LSFElement, PsiReference {

    LSFId getSimpleName(); // getSimpleName чтобы по умолчанию подтянуть реализации
}
