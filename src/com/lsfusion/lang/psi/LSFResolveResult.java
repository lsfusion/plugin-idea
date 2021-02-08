package com.lsfusion.lang.psi;

import com.intellij.lang.annotation.AnnotationHolder;
import com.lsfusion.lang.LSFResolvingError;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;
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
    
    public LSFResolvingError resolveErrorAnnotation(AnnotationHolder holder) {
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

        public abstract LSFResolvingError resolveErrorAnnotation(AnnotationHolder holder);
    }

    public static class AmbigiousErrorAnnotator extends ErrorAnnotator {

        public AmbigiousErrorAnnotator(LSFReferenceImpl ref, Collection<? extends LSFDeclaration> decls) {
            super(ref, decls);
        }

        public LSFResolvingError resolveErrorAnnotation(AnnotationHolder holder) {
            return ref.resolveAmbiguousErrorAnnotation(decls);
        }
    }

    
    public static class NotFoundErrorAnnotator extends ErrorAnnotator {
        
        private boolean canBeDeclaredAfterAndNotChecked;

        public NotFoundErrorAnnotator(LSFReferenceImpl ref, Collection<? extends LSFDeclaration> decls, boolean canBeDeclaredAfterAndNotChecked) {
            super(ref, decls);
            
            this.canBeDeclaredAfterAndNotChecked = canBeDeclaredAfterAndNotChecked;
        }

        public LSFResolvingError resolveErrorAnnotation(AnnotationHolder holder) {
            if(decls.size() == 1) {
                LSFDeclaration singleDecl = decls.iterator().next();
                if(singleDecl instanceof LSFFullNameDeclaration && LSFGlobalResolver.isAfter(ref.getLSFFile(), ref.getTextOffset(), (LSFFullNameDeclaration) singleDecl)) {
                    String errorText = "Symbol '" + ref.getNameRef() + "' is declared after it is used";
                    return new LSFResolvingError(ref, errorText, true);
                }
            }
            return ref.resolveNotFoundErrorAnnotation(decls, canBeDeclaredAfterAndNotChecked);
        }
    }
}
