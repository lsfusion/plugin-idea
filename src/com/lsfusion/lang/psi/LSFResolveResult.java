package com.lsfusion.lang.psi;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;

import java.util.Collection;

public class LSFResolveResult {
    public Collection<? extends LSFDeclaration> declarations;
    public ErrorAnnotator errorAnnotator;

    public LSFResolveResult(Collection<? extends LSFDeclaration> declarations) {
        this(declarations, null);
    }
    
    public LSFResolveResult(Collection<? extends LSFDeclaration> declarations, ErrorAnnotator errorAnnotator) {
        this.declarations = declarations;
        this.errorAnnotator = errorAnnotator;
    }
    
    public Annotation resolveErrorAnnotation(AnnotationHolder holder) {
        if (errorAnnotator != null) {
            return errorAnnotator.resolveErrorAnnotation(holder);
        } else {
            return null;
        }
    }
    
    public static abstract class ErrorAnnotator {
        public abstract Annotation resolveErrorAnnotation(AnnotationHolder holder);
    }
}
