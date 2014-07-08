package com.lsfusion.references;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
import com.lsfusion.actions.ShowErrorsAction;
import com.lsfusion.lang.LSFReferenceAnnotator;
import org.jetbrains.annotations.NotNull;

public class FromJavaReferenceAnnotator implements Annotator {
    private boolean errorsSearchMode = false;

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof PsiLiteralExpression) {
            PsiLiteralExpression psiLiteral = (PsiLiteralExpression) element;
            Object value = psiLiteral.getValue();
            if (value instanceof String) {
                PsiReference ref = psiLiteral.findReferenceAt(element.getTextLength() - 1);
                if (ref instanceof PsiMultiReference) {
                    PsiMultiReference multiRef = (PsiMultiReference) ref;
                    for (PsiReference r : multiRef.getReferences()) {
                        if (r instanceof FromJavaReference) {
                            ref = r;
                            break;
                        }
                    }
                }
                if (ref instanceof FromJavaReference) {
                    FromJavaReference fromJavaRef = (FromJavaReference) ref;
                    ResolveResult[] result = fromJavaRef.multiResolve(false);
                    if (result.length == 0) {
                        addError(fromJavaRef, holder, "Cannot resolve symbol '" + fromJavaRef.referenceText + "'", LSFReferenceAnnotator.ERROR);
                    } else if (result.length > 1) {
                        addError(fromJavaRef, holder, "Ambiguous reference", LSFReferenceAnnotator.WAVE_UNDERSCORED_ERROR);
                    }
                }
            }
        }
    }

    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder, boolean errorsSearchMode) {
        this.errorsSearchMode = errorsSearchMode;
        annotate(psiElement, holder);
    }

    private void addError(FromJavaReference fromJavaRef, AnnotationHolder holder, String text, TextAttributes textAttributes) {
        if (errorsSearchMode) {
            ShowErrorsAction.showErrorMessage(fromJavaRef.getElement(), text);
        }

        Annotation annotation = holder.createWarningAnnotation(fromJavaRef.getRangeInDocument(), text);
        annotation.setEnforcedTextAttributes(textAttributes);
    }
}
