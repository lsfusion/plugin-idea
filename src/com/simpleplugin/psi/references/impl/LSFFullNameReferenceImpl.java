package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.util.Query;
import com.simpleplugin.psi.*;
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
    public LSFId getSimpleName() {
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
    protected Condition<T> getCondition() {
        return Condition.TRUE;
    }
    @Override
    public Query<T> resolveNoCache() {
        return LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), (LSFFile) getContainingFile(), getType(), getCondition());
    }
}
