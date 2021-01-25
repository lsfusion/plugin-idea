package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.EmptyQuery;
import com.intellij.util.Query;
import com.lsfusion.lang.psi.LSFDesignStatement;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.LSFResolveResult;
import com.lsfusion.lang.psi.context.FormContext;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFDesignElementDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFormElementDeclaration;
import com.lsfusion.lang.psi.extend.LSFDesign;
import com.lsfusion.lang.psi.references.LSFDesignElementReference;
import com.lsfusion.lang.psi.references.LSFFormElementReference;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class LSFDesignElementReferenceImpl<T extends LSFDesignElementDeclaration<T>> extends LSFReferenceImpl<T> implements LSFDesignElementReference<T> {

    public LSFDesignElementReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public LSFResolveResult resolveNoCache() {
        final List<T> objects = new ArrayList<>();
        if (getSimpleName() != null) {
            Condition<T> filter = getResolvedDeclarationsFilter();
            for (T decl : processDesignContext(this, getTextOffset(), getElementsCollector())) {
                if (filter.value(decl)) {
                    objects.add(decl);
                }
            }
        }
        return new LSFResolveResult(objects, resolveDefaultErrorAnnotator(objects, true));
    }

    protected Condition<T> getResolvedDeclarationsFilter() {
        final String nameRef = getNameRef();
        return decl -> {
            String name = decl.getDeclName();
            if (name != null) {
                return decl.getDeclName().equals(nameRef);
            }
            return false;
        };
    }

    protected abstract DesignProcessor<T> getElementsCollector();

    public interface DesignProcessor<T extends LSFDeclaration> {
        Collection<T> process(LSFDesign design);
    }

    public static <T extends LSFDesignElementDeclaration<T>> Set<T> processDesignContext(PsiElement current, int offset, final DesignProcessor<T> processor) {
        Set<T> processedContext = processDesignContext(current, offset, processor, true);
        if (processedContext != null) {
            return processedContext;
        }

        PsiElement parent = current.getParent();
        if (!(parent == null || parent instanceof LSFFile)) {
            return processDesignContext(parent, offset, processor); // бежим выше
        }

        return new HashSet<>();
    }

    public static <T extends LSFDesignElementDeclaration<T>> Set<T> processDesignContext(PsiElement current, int offset, final DesignProcessor<T> processor, boolean objectRef) {
        Query<LSFDesign> designs = null;
        if (current instanceof FormContext && (objectRef || current instanceof LSFDesignStatement)) {
            LSFFormDeclaration designDecl = ((FormContext) current).resolveFormDecl();
            designs = designDecl == null
                    ? new EmptyQuery<LSFDesign>()
                    : LSFGlobalResolver.findExtendElements(designDecl, LSFStubElementTypes.DESIGN, (LSFFile) current.getContainingFile());
        }

        if (designs != null) {
            final Set<T> finalResult = new HashSet<>();
            final PsiFile currentFile = current.getContainingFile();
            designs.forEach(design -> {
                boolean sameFile = currentFile == design.getLSFFile();
                for(T element : processor.process(design))
                    if(!(sameFile && LSFGlobalResolver.isAfter(offset, element)))
                        finalResult.add(element);
                return true;
            });
            return finalResult;
        }
        return null;
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
