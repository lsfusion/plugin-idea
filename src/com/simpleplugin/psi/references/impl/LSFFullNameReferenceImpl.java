package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.util.Query;
import com.simpleplugin.psi.LSFCompoundID;
import com.simpleplugin.psi.LSFFile;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFFullNameDeclaration;
import com.simpleplugin.psi.references.LSFFullNameReference;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public abstract class LSFFullNameReferenceImpl<T extends LSFFullNameDeclaration> extends LSFGlobalReferenceImpl<T> implements LSFFullNameReference<T> {

    protected LSFFullNameReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected abstract LSFCompoundID getCompoundID();

    @Override
    public LSFSimpleName getSimpleName() {
        Iterator<LSFSimpleName> compoundID = getCompoundID().getSimpleNameList().iterator();
        LSFSimpleName firstChild = compoundID.next();
        if(!compoundID.hasNext())
            return firstChild;
        else
            return compoundID.next();
    }

    public String getFullNameRef() {
        Iterator<LSFSimpleName> compoundID = getCompoundID().getSimpleNameList().iterator();
        LSFSimpleName firstChild = compoundID.next();
        if(compoundID.hasNext())
            return firstChild.getText();
        return null;
    }

    protected abstract FullNameStubElementType getType();
    protected Query<T> adjustResolve(Query<T> resolved) {
        return resolved;
    }

    @Override
    public Query<T> resolveNoCache() {
        return adjustResolve(LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), (LSFFile) getContainingFile(), getType()));
    }
}
