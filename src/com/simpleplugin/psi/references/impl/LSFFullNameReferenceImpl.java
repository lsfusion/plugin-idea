package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.simpleplugin.LSFDeclarationResolveResult;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFDeclaration;
import com.simpleplugin.psi.declarations.LSFFullNameDeclaration;
import com.simpleplugin.psi.references.LSFFullNameReference;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    protected FullNameStubElementType getType() {
        return null;
    }

    protected Collection<FullNameStubElementType> getTypes() {
        return Collections.singleton(getType());
    }

    protected Condition<G> getCondition() {
        return Condition.TRUE;
    }

    protected Finalizer<G> getFinalizer() {
        return Finalizer.EMPTY;
    }

    @Override
    public LSFDeclarationResolveResult resolveNoCache() {
        Collection<G> decls = LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), getLSFFile(), getTypes(), getCondition(), getFinalizer()); 
        return new LSFDeclarationResolveResult(decls, resolveDefaultErrorAnnotator(decls));
    }
    
    @Override
    protected Collection<StringStubIndexExtension> getGlobalIndices() {
        List<StringStubIndexExtension> result = new ArrayList<StringStubIndexExtension>();
        for(FullNameStubElementType type : getTypes())
            result.add(type.getGlobalIndex());
        return result;
    }
}
