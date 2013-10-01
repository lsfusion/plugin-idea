package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.CollectionQuery;
import com.intellij.util.EmptyQuery;
import com.intellij.util.Query;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.context.FormContext;
import com.simpleplugin.psi.declarations.LSFDeclaration;
import com.simpleplugin.psi.extend.LSFFormExtend;
import com.simpleplugin.psi.references.LSFFormElementReference;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class LSFFormElementReferenceImpl<T extends LSFDeclaration> extends LSFReferenceImpl<T> implements LSFFormElementReference<T> {

    protected LSFFormElementReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isSoft() {
        return false;
    }
    
    public static interface Processor<T> {
        Collection<T> process(LSFFormExtend formExtend);        
    }

    private static <T> Set<T> processParams(PsiElement current, int offset, final Processor<T> processor) {
        Set<T> processedContext = processFormContext(current, processor, true);
        if(processedContext!=null)
            return processedContext;

        PsiElement parent = current.getParent();
        if(!(parent == null || parent instanceof LSFFile))
            return processParams(parent, offset, processor); // бежим выше

        return new HashSet<T>();
    }

    public static <T> Set<T> processFormContext(PsiElement current, final Processor<T> processor, boolean objectRef) {
        Query<LSFFormExtend> extendForms = null;
        if (current instanceof LSFDesignStatement || (current instanceof FormContext && objectRef)) {
            LSFFormUsage formUsage;
            if(current instanceof LSFDesignStatement) {
                LSFDesignStatement design = (LSFDesignStatement) current;
                LSFDesignDeclaration designDecl = design.getDesignDeclaration();
                if(designDecl!=null)
                    formUsage = designDecl.getFormUsage();
                else
                    formUsage = design.getExtendDesignDeclaration().getFormUsage();
            } else
                formUsage = ((FormContext)current).getFormUsage();
            extendForms = LSFGlobalResolver.findExtendElements(formUsage.resolveDecl(), LSFStubElementTypes.EXTENDFORM, formUsage.getLSFFile());
        } else if (current instanceof LSFFormStatement) {
            LSFFormExtend currentForm = ((LSFFormStatement)current);
            extendForms = LSFGlobalResolver.findExtendElements(currentForm);
        }
        if(extendForms!=null) {
            final Set<T> finalResult = new HashSet<T>();
            extendForms.forEach(new com.intellij.util.Processor<LSFFormExtend>() {
                public boolean process(LSFFormExtend formExtend) {
                    finalResult.addAll(processor.process(formExtend));
                    return true;
                }
            });
            return finalResult;
        }
        return null;
    }

    protected abstract Processor<T> getProcessor();

    private Set<T> processParams() {
        return processParams(this, getTextOffset(), getProcessor());
    }

    @Override
    public Query<T> resolveNoCache() {
        if(getSimpleName()==null)
            return new EmptyQuery<T>();

        final String nameRef = getNameRef();
        final List<T> objects = new ArrayList<T>();
        for(T decl : processParams())
            if(decl.getDeclName().equals(nameRef))
                objects.add((T) decl);
        return new CollectionQuery<T>(objects);
    }

    @Override
    protected void fillListVariants(final Collection<String> variants) {
        for(T decl : processParams())
            variants.add(decl.getDeclName());
    }

}
