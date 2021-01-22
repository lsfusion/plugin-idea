package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.EmptyQuery;
import com.intellij.util.Query;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.FormContext;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFormElementDeclaration;
import com.lsfusion.lang.psi.declarations.impl.LSFFormExtendElement;
import com.lsfusion.lang.psi.extend.LSFDesign;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFFormElementReference;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class LSFFormElementReferenceImpl<T extends LSFFormElementDeclaration> extends LSFReferenceImpl<T> implements LSFFormElementReference<T> {
    
    protected LSFFormElementReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isSoft() {
        return false;
    }

    @Override
    public LSFResolveResult resolveNoCache() {
        final List<T> objects = new ArrayList<>();
        if (getSimpleName() != null) {
            Condition<T> filter = getResolvedDeclarationsFilter();
            for (T decl : processFormContext(this, getTextOffset(), getElementsCollector())) {
                if (filter.value(decl)) {
                    objects.add(decl);
                }
            }
        }
        return new LSFResolveResult(objects, resolveDefaultErrorAnnotator(objects, true));
    }

    protected Condition<T> getResolvedDeclarationsFilter() {
        final String nameRef = getNameRef();
        return new Condition<T>() {
            public boolean value(T decl) {
                String name = decl.getDeclName();
                if (name != null) {
                    return decl.getDeclName().equals(nameRef);
                } 
                return false;
            }
        };
    }

    protected abstract FormExtendProcessor<T> getElementsCollector();

    public interface FormExtendProcessor<T extends LSFFormExtendElement> {
        Collection<T> process(LSFFormExtend formExtend);
    }

    public static <T extends LSFFormExtendElement> Set<T> processFormContext(PsiElement current, int offset, final FormExtendProcessor<T> processor) {
        Set<T> processedContext = processFormContext(current, processor, offset, true, false);
        if (processedContext != null) {
            return processedContext;
        }

        PsiElement parent = current.getParent();
        if (!(parent == null || parent instanceof LSFFile)) {
            return processFormContext(parent, offset, processor); // бежим выше
        }

        return new HashSet<>();
    }

    public static <T extends LSFFormExtendElement> Set<T> processFormContext(PsiElement current, final FormExtendProcessor<T> processor, final int offset, boolean objectRef, boolean ignoreUseBeforeDeclarationCheck) {
        Query<LSFFormExtend> extendForms = null;
        if (current instanceof FormContext && (objectRef || current instanceof LSFFormStatement || current instanceof LSFDesignStatement)) {
            LSFFormDeclaration formDecl = ((FormContext) current).resolveFormDecl();
            extendForms = formDecl == null
                          ? new EmptyQuery<LSFFormExtend>()
                          : LSFGlobalResolver.findExtendElements(formDecl, LSFStubElementTypes.EXTENDFORM, (LSFFile) current.getContainingFile());
        }

        if (extendForms != null) {
            final Set<T> finalResult = new HashSet<>();
            final PsiFile currentFile = current.getContainingFile();
            extendForms.forEach(new com.intellij.util.Processor<LSFFormExtend>() {
                public boolean process(LSFFormExtend formExtend) {
                    boolean sameFile = currentFile == formExtend.getLSFFile();
                    for(T element : processor.process(formExtend))
                        if(ignoreUseBeforeDeclarationCheck || !(sameFile && LSFGlobalResolver.isAfter(offset, element)))
                            finalResult.add(element);
                    return true;
                }
            });
            return finalResult;
        }
        return null;
    }

    public interface DesignProcessor<T> {
        Collection<T> process(LSFDesign design);
    }

    public static <T> Set<T> processDesignContext(PsiElement current, int offset, final DesignProcessor<T> processor) {
        Set<T> processedContext = processDesignContext(current, processor, true);
        if (processedContext != null) {
            return processedContext;
        }

        PsiElement parent = current.getParent();
        if (!(parent == null || parent instanceof LSFFile)) {
            return processDesignContext(parent, offset, processor); // бежим выше
        }

        return new HashSet<>();
    }

    public static <T> Set<T> processDesignContext(PsiElement current, final DesignProcessor<T> processor, boolean objectRef) {
        Query<LSFDesign> designs = null;
        if (current instanceof FormContext && (objectRef || current instanceof LSFDesignStatement)) {
            LSFFormDeclaration designDecl = ((FormContext) current).resolveFormDecl();
            designs = designDecl == null
                    ? new EmptyQuery<LSFDesign>()
                    : LSFGlobalResolver.findExtendElements(designDecl, LSFStubElementTypes.DESIGN, (LSFFile) current.getContainingFile());
        }

        if (designs != null) {
            final Set<T> finalResult = new HashSet<>();
            designs.forEach(new com.intellij.util.Processor<LSFDesign>() {
                public boolean process(LSFDesign design) {
                    finalResult.addAll(processor.process(design));
                    return true;
                }
            });
            return finalResult;
        }
        return null;
    }
}
