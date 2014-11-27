package com.lsfusion.lang.psi;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.references.impl.LSFReferenceImpl;

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

        protected LSFReferenceImpl ref;
        protected Collection<? extends LSFDeclaration> decls;
        
        protected ErrorAnnotator(LSFReferenceImpl ref, Collection<? extends LSFDeclaration> decls) {
            this.ref = ref;
            this.decls = decls;
            
            assert decls != null;
        }

        public abstract Annotation resolveErrorAnnotation(AnnotationHolder holder);
    }

    public static class AmbigiousErrorAnnotator extends ErrorAnnotator {

        public AmbigiousErrorAnnotator(LSFReferenceImpl ref, Collection<? extends LSFDeclaration> decls) {
            super(ref, decls);
        }

        public Annotation resolveErrorAnnotation(AnnotationHolder holder) {
            return ref.resolveAmbiguousErrorAnnotation(holder, decls);
        }
    }

    
    public static class NotFoundErrorAnnotator extends ErrorAnnotator {

        public NotFoundErrorAnnotator(LSFReferenceImpl ref, Collection<? extends LSFDeclaration> decls) {
            super(ref, decls);
        }

        public Annotation resolveErrorAnnotation(AnnotationHolder holder) {
            return ref.resolveNotFoundErrorAnnotation(holder, decls);
        }
    }
}
