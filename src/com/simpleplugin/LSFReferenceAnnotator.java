package com.simpleplugin;

import com.intellij.codeInsight.daemon.EmptyResolveMessageProvider;
import com.intellij.codeInsight.daemon.QuickFixProvider;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.simpleplugin.psi.*;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.List;

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

    private void checkReference(LSFElementReference reference) {
        if (!reference.isSoft() && reference.resolve() == null)
            addError(reference);
    }

    private void addError(LSFElementReference reference) {
        final Annotation annotation = myHolder.createErrorAnnotation(reference.getTextRange(), "Cannot resolve symbol");
        annotation.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
    }
}

