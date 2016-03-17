package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.util.EmptyQuery;
import com.intellij.util.Query;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFFormStatement;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.LSFResolveResult;
import com.lsfusion.lang.psi.context.FormContext;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFFormElementReference;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
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

    @Override
    public LSFResolveResult resolveNoCache() {
        final List<T> objects = new ArrayList<>();
        if (getSimpleName() != null) {
            Condition<T> filter = getResolvedDeclarationsFilter();
            for (T decl : collectElementsFromContext()) {
                if (filter.value(decl)) {
                    objects.add(decl);
                }
            }
        }
        return new LSFResolveResult(objects, resolveDefaultErrorAnnotator(objects));
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

    private Set<T> collectElementsFromContext() {
        return processFormContext(this, getTextOffset(), getElementsCollector());
    }

    protected abstract FormExtendProcessor<T> getElementsCollector();

    public interface FormExtendProcessor<T> {
        Collection<T> process(LSFFormExtend formExtend);
    }

    public static <T> Set<T> processFormContext(PsiElement current, int offset, final FormExtendProcessor<T> processor) {
        Set<T> processedContext = processFormContext(current, processor, true);
        if (processedContext != null) {
            return processedContext;
        }

        PsiElement parent = current.getParent();
        if (!(parent == null || parent instanceof LSFFile)) {
            return processFormContext(parent, offset, processor); // бежим выше
        }

        return new HashSet<>();
    }

    public static <T> Set<T> processFormContext(PsiElement current, final FormExtendProcessor<T> processor, boolean objectRef) {
        Query<LSFFormExtend> extendForms = null;
        if (current instanceof FormContext && (objectRef || current instanceof LSFFormStatement)) {
            LSFFormDeclaration formDecl = ((FormContext) current).resolveFormDecl();
            extendForms = formDecl == null
                          ? new EmptyQuery<LSFFormExtend>()
                          : LSFGlobalResolver.findExtendElements(formDecl, LSFStubElementTypes.EXTENDFORM, (LSFFile) current.getContainingFile());
        }

        if (extendForms != null) {
            final Set<T> finalResult = new HashSet<>();
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
}
