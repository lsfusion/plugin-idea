package com.lsfusion.psi.references;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.lsfusion.psi.LSFResolveResult;
import com.lsfusion.psi.LSFElement;
import com.lsfusion.psi.LSFId;
import com.lsfusion.psi.declarations.LSFDeclaration;
import org.jetbrains.annotations.Nullable;

public interface LSFReference<T extends LSFDeclaration> extends LSFElement, PsiReference, ItemPresentation, PsiNamedElement {

    LSFId getSimpleName(); // getSimpleName чтобы по умолчанию подтянуть реализации

    LSFId resolve();

    @Nullable
    T resolveDecl();

    String getNameRef();

    LSFResolveResult resolveNoCache();

    Annotation resolveErrorAnnotation(AnnotationHolder holder);
}
