package com.simpleplugin.psi.references.impl;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.IncorrectOperationException;
import com.simpleplugin.LSFIcons;
import com.simpleplugin.LSFReferenceAnnotator;
import com.simpleplugin.psi.LSFElementImpl;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFResolveUtil;
import com.simpleplugin.psi.LSFResolver;
import com.simpleplugin.psi.declarations.LSFDeclaration;
import com.simpleplugin.psi.references.LSFReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class LSFReferenceImpl<T extends LSFDeclaration> extends LSFElementImpl implements LSFReference<T> {

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
        TextRange nameRange = getSimpleName().getTextRange();

        return new TextRange(
                nameRange.getStartOffset() - textRange.getStartOffset(),
                nameRange.getEndOffset() - textRange.getStartOffset()
        );
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getText(); // пока так
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        getSimpleName().setName(newElementName);
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

    @Override
    public String getNameRef() {
        return getSimpleName().getText();
    }

    @Override
    public LSFId resolve() {
        T decl = resolveDecl();
        if(decl == null)
            return null;
        return decl.getNameIdentifier();
    }

    public T resolveDecl() {
        final ResolveResult[] resolveResults = multiResolveDecl(true);

        return resolveResults.length == 0 ||
                resolveResults.length > 1 ||
                !resolveResults[0].isValidResult() ? null : (T) resolveResults[0].getElement();
    }

    @NotNull
    public ResolveResult[] multiResolveDecl(boolean incompleteCode) {
        final List<? extends PsiElement> elements =
                ResolveCache.getInstance(getProject()).resolveWithCaching(this, LSFResolver.INSTANCE, true, incompleteCode);
        return LSFResolveUtil.toCandidateInfoArray(elements);
    }

    @Override
    public Annotation resolveErrorAnnotation(AnnotationHolder holder) {
        ResolveResult[] resolveResults = multiResolveDecl(true);
        if (resolveResults.length == 0) {
            return resolveNotFoundErrorAnnotation(holder);
        } else if (resolveResults.length > 1) {
            String ambError = "Ambiguous reference: ";
            for (ResolveResult result : resolveResults) {
//                ambError += "\n" + getNameRef() + result.  
            }
            
            Annotation annotation = holder.createErrorAnnotation(getTextRange(), ambError);
            annotation.setEnforcedTextAttributes(LSFReferenceAnnotator.WAVE_UNDERSCORED_ERROR);
            return annotation; 
        }
        
        return null;
    }
    
    public Annotation resolveNotFoundErrorAnnotation(AnnotationHolder holder) {
        return holder.createErrorAnnotation(getTextRange(), "Cannot resolve symbol");
    }
    
    protected abstract void fillListVariants(Collection<String> variants);

    @NotNull
    @Override
    public Object[] getVariants() {
        List<String> stringVariants = new ArrayList<String>();
        fillListVariants(stringVariants);
        
        List<LookupElement> variants = new ArrayList<LookupElement>();
        for (final String property : stringVariants) {
            variants.add(LookupElementBuilder.create(property).
                    withIcon(LSFIcons.FILE).
                    withTypeText(getLSFFile().getName())
            );
        }
        return variants.toArray();
    }
}
