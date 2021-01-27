package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.lsfusion.lang.psi.LSFResolveResult;
import com.lsfusion.lang.psi.declarations.LSFDesignElementDeclaration;
import com.lsfusion.lang.psi.extend.LSFDesign;
import com.lsfusion.lang.psi.extend.impl.LSFDesignImpl;
import com.lsfusion.lang.psi.references.LSFDesignElementReference;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public abstract class LSFDesignElementReferenceImpl<T extends LSFDesignElementDeclaration<T>> extends LSFReferenceImpl<T> implements LSFDesignElementReference<T> {

    public LSFDesignElementReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public LSFResolveResult resolveNoCache() {
        final List<T> objects = new ArrayList<>();
        if (getSimpleName() != null) {
            Condition<T> filter = getResolvedDeclarationsFilter();
            for (T decl : LSFDesignImpl.processDesignContext(this, getTextOffset(), getElementsCollector())) {
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

    protected abstract Function<LSFDesign, Collection<T>> getElementsCollector();

    @Override
    public boolean isSoft() {
        return false;
    }
}
