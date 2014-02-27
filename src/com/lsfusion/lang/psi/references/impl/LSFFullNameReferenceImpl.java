package com.lsfusion.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.lsfusion.psi.LSFResolveResult;
import com.lsfusion.psi.*;
import com.lsfusion.psi.declarations.LSFDeclaration;
import com.lsfusion.psi.declarations.LSFFullNameDeclaration;
import com.lsfusion.psi.references.LSFFullNameReference;
import com.lsfusion.psi.stubs.types.FullNameStubElementType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public abstract class LSFFullNameReferenceImpl<T extends LSFDeclaration, G extends LSFFullNameDeclaration> extends LSFGlobalReferenceImpl<T> implements LSFFullNameReference<T, G> {

    protected LSFFullNameReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected abstract LSFCompoundID getCompoundID();

    public static LSFId getSimpleName(LSFCompoundID compoundID) {
        return compoundID.getSimpleName();
    }
    
    @Override
    public LSFId getSimpleName() {
        return getSimpleName(getCompoundID());
    }

    public String getFullNameRef() {
        LSFNamespaceUsage namespace = getCompoundID().getNamespaceUsage();
        return namespace == null ? null : namespace.getText();
    }

    protected Collection<FullNameStubElementType> getStubElementTypes() {
        return Collections.singleton(getStubElementType());
    }

    protected FullNameStubElementType getStubElementType() {
        return null;
    }

    protected Condition<G> getCondition() {
        return Condition.TRUE;
    }

    protected Finalizer<G> getFinalizer() {
        return Finalizer.EMPTY;
    }

    @Override
    public LSFResolveResult resolveNoCache() {
        Collection<G> decls = LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), getLSFFile(), getStubElementTypes(), getCondition(), getFinalizer());
        return new LSFResolveResult(decls, resolveDefaultErrorAnnotator(decls));
    }
}
