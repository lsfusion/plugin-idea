package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFFormElementDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.extend.impl.LSFFormExtendImpl;
import com.lsfusion.lang.psi.references.LSFFormElementReference;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

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
            for (T decl : LSFFormExtendImpl.processFormContext(this, getTextOffset(), LSFLocalSearchScope.createFrom(this), getElementsCollector())) {
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

    protected abstract Function<LSFFormExtend, Collection<T>> getElementsCollector();

}
