package com.simpleplugin;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.simpleplugin.psi.declarations.LSFDeclaration;

import java.util.Collection;

public class LSFDeclarationResolveResult {
    public Collection<? extends LSFDeclaration> declarations;
    public ErrorAnnotator errorAnnotator;

    public LSFDeclarationResolveResult(Collection<? extends LSFDeclaration> declarations) {
        this(declarations, null);
    }
    
    public LSFDeclarationResolveResult(Collection<? extends LSFDeclaration> declarations, ErrorAnnotator errorAnnotator) {
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
