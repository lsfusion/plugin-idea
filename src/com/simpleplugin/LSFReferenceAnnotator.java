package com.simpleplugin;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.references.LSFGlobalReference;
import org.jetbrains.annotations.NotNull;

public class LSFReferenceAnnotator extends LSFVisitor implements Annotator {
    private AnnotationHolder myHolder;

    @Override
    public synchronized void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder) {
        myHolder = holder;
        try {
            psiElement.accept(this);
        } finally {
            myHolder = null;
        }
    }

    @Override
    public void visitPropertyUsage(@NotNull LSFPropertyUsage o) {
        checkReference(o);
    }

    @Override
    public void visitCustomClassUsage(@NotNull LSFCustomClassUsage o) {
        checkReference(o);
    }

    @Override
    public void visitMetaCodeStatement(@NotNull LSFMetaCodeStatement o) {
        checkReference(o);
    }

    private void checkReference(LSFGlobalReference reference) {
        if (!reference.isSoft() && reference.resolve() == null)
            addError(reference);
    }

    private void addError(LSFGlobalReference reference) {
        final Annotation annotation = myHolder.createErrorAnnotation(reference.getTextRange(), "Cannot resolve symbol");
        annotation.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
    }
}

