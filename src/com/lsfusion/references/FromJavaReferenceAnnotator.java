package com.lsfusion.references;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
import com.lsfusion.lang.LSFReferenceAnnotator;
import org.jetbrains.annotations.NotNull;

public class FromJavaReferenceAnnotator implements Annotator {
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
                        Annotation annotation = holder.createWarningAnnotation(fromJavaRef.getRangeInDocument(), "Cannot resolve symbol '" + fromJavaRef.referenceText + "'");
                        annotation.setEnforcedTextAttributes(LSFReferenceAnnotator.ERROR);
                    } else if (result.length > 1) {
                        Annotation annotation = holder.createWarningAnnotation(fromJavaRef.getRangeInDocument(), "Ambiguous reference");
                        annotation.setEnforcedTextAttributes(LSFReferenceAnnotator.WAVE_UNDERSCORED_ERROR);
                    }
                }
            }
        }
    }
}
