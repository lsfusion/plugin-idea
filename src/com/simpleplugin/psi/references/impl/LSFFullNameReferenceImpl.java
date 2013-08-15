package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.util.EmptyQuery;
import com.intellij.util.MergeQuery;
import com.intellij.util.Query;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFDeclaration;
import com.simpleplugin.psi.declarations.LSFFullNameDeclaration;
import com.simpleplugin.psi.references.LSFFullNameReference;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class LSFFullNameReferenceImpl<T extends LSFDeclaration, G extends LSFFullNameDeclaration> extends LSFGlobalReferenceImpl<T> implements LSFFullNameReference<T, G> {

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

    protected FullNameStubElementType getType() {
        return null;
    }

    protected Collection<FullNameStubElementType> getTypes() {
        return Collections.singleton(getType());
    }

    protected Condition<G> getCondition() {
        return Condition.TRUE;
    }
    @Override
    public Query<T> resolveNoCache() {
        Query<T> result = null;
        for(FullNameStubElementType type : getTypes()) {
            Query<T> typeFind = LSFGlobalResolver.findElements(getNameRef(), getFullNameRef(), getLSFFile(), type, getCondition());
            if(result == null)
                result = typeFind;
            else            
                result = new MergeQuery<T>(result, typeFind);
        }
        return result;
    }
    
    @Override
    protected Collection<StringStubIndexExtension> getGlobalIndices() {
        List<StringStubIndexExtension> result = new ArrayList<StringStubIndexExtension>();
        for(FullNameStubElementType type : getTypes())
            result.add(type.getGlobalIndex());
        return result;
    }
}
