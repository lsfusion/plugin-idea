package com.lsfusion.lang.psi.references;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiReference;
import com.lsfusion.lang.LSFResolvingError;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.LSFElement;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFResolveResult;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import org.jetbrains.annotations.Nullable;

public interface LSFReference<T extends LSFDeclaration> extends LSFElement, PsiReference, ItemPresentation {

    LSFId getSimpleName(); // getSimpleName чтобы по умолчанию подтянуть реализации

    LSFId resolve();

    @Nullable
    T resolveDecl();
    
    String getName();

    String getNameRef();

    LSFResolveResult resolveNoCache();

    LSFResolvingError resolveErrorAnnotation(AnnotationHolder holder);

    void handleElementRename(String newElementName, MetaTransaction transaction);

}
