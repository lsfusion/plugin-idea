package com.lsfusion.psi.declarations;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.lsfusion.psi.LSFElement;
import com.lsfusion.psi.LSFId;
import org.jetbrains.annotations.Nullable;

public interface LSFDeclaration extends LSFElement, PsiNameIdentifierOwner, ItemPresentation {

    String getDeclName();

    @Nullable
    @Override
    LSFId getNameIdentifier();

    boolean resolveDuplicates();

    PsiElement[] processImplementationsSearch();

    PsiElement[] processExtensionsSearch();
}
