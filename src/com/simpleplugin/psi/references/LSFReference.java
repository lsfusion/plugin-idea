package com.simpleplugin.psi.references;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.Query;
import com.simpleplugin.psi.LSFElement;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFDeclaration;
import org.jetbrains.annotations.Nullable;

public interface LSFReference<T extends LSFDeclaration> extends LSFElement, PsiReference {

    LSFId getSimpleName(); // getSimpleName чтобы по умолчанию подтянуть реализации

    LSFId resolve();
    @Nullable
    T resolveDecl();

    String getNameRef();

    Query<T> resolveNoCache();
}
