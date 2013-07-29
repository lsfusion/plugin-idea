package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.util.FilteredQuery;
import com.intellij.util.Query;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFMetaDeclaration;
import com.simpleplugin.psi.references.LSFMetaReference;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public abstract class LSFMetaReferenceImpl extends LSFFullNameReferenceImpl<LSFMetaDeclaration> implements LSFMetaReference {

    public LSFMetaReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected FullNameStubElementType<?, LSFMetaDeclaration> getType() {
        return LSFStubElementTypes.META;
    }

    protected abstract LSFMetacodeUsage getMetacodeUsage();

    protected abstract LSFMetaCodeIdList getMetaCodeIdList();

    @Override
    protected LSFCompoundID getCompoundID() {
        return getMetacodeUsage().getCompoundID();
    }

    @Override
    protected Condition<LSFMetaDeclaration> getCondition() {
        final int paramCount = getMetaCodeIdList().getMetaCodeIdList().size();
        return new Condition<LSFMetaDeclaration>() {
            public boolean value(LSFMetaDeclaration decl) {
                return decl.getParamCount()==paramCount;
            }
        };
    }
}
