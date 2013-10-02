package com.simpleplugin.psi.declarations;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.simpleplugin.psi.LSFElement;
import com.simpleplugin.psi.LSFId;
import org.jetbrains.annotations.Nullable;

public interface LSFDeclaration extends LSFElement, PsiNameIdentifierOwner, ItemPresentation {

    String getDeclName();

    @Nullable
    @Override
    LSFId getNameIdentifier();
    
    String getPresentableDeclText();
}
