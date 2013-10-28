package com.simpleplugin.psi.references;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.simpleplugin.LSFDeclarationResolveResult;
import com.simpleplugin.psi.LSFElement;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.declarations.LSFDeclaration;
import org.jetbrains.annotations.Nullable;

public interface LSFReference<T extends LSFDeclaration> extends LSFElement, PsiReference, ItemPresentation, PsiNamedElement {

    LSFId getSimpleName(); // getSimpleName чтобы по умолчанию подтянуть реализации

    LSFId resolve();

    @Nullable
    T resolveDecl();

    String getNameRef();

    LSFDeclarationResolveResult resolveNoCache();

    Annotation resolveErrorAnnotation(AnnotationHolder holder);
}
