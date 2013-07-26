package com.simpleplugin.psi.declarations;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.tree.IElementType;
import com.simpleplugin.psi.LSFSimpleName;
import org.jetbrains.annotations.Nullable;

public interface LSFDeclaration extends PsiNameIdentifierOwner {

    @Nullable
    @Override
    LSFSimpleName getNameIdentifier();
}
