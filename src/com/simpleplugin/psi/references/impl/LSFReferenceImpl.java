package com.simpleplugin.psi.references.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.simpleplugin.psi.declarations.impl.LSFDeclarationImpl;
import com.simpleplugin.psi.references.LSFReference;
import org.jetbrains.annotations.NotNull;

public abstract class LSFReferenceImpl extends ASTWrapperPsiElement implements LSFReference {

    public LSFReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    @Override
    public PsiElement getElement() {
        return this;
    }

    @Override
    public TextRange getRangeInElement() {
        final TextRange textRange = getTextRange();
        return new TextRange(0, textRange.getEndOffset() - textRange.getStartOffset());
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getText(); // пока так
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        LSFDeclarationImpl.setName(getSimpleName(), getProject(), newElementName);
        return this;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return this;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return resolve() == element;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
