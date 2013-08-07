package com.simpleplugin.psi.declarations;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.tree.IElementType;
import com.simpleplugin.psi.LSFElement;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.references.LSFReference;
import org.jetbrains.annotations.Nullable;

public interface LSFDeclaration extends LSFElement, PsiNameIdentifierOwner {

    @Nullable
    @Override
    LSFId getNameIdentifier();
}
