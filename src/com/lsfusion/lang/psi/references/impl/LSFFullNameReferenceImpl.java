package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;
import com.lsfusion.lang.psi.references.LSFFullNameReference;
import com.lsfusion.lang.psi.stubs.FullNameStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.util.BaseUtils;
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
    public void setFullNameRef(String name, MetaTransaction transaction) {
        LSFCompoundID compoundIDFromText = LSFElementGenerator.createCompoundIDFromText(getProject(), name + "." + getNameRef());
        if(transaction!=null) {
            assert getFullNameRef() == null;
            ASTNode namespaceNode = compoundIDFromText.getNamespaceUsage().getNode();
            transaction.regChange(BaseUtils.toList(namespaceNode, namespaceNode.getTreeNext()), getSimpleName().getNode(), MetaTransaction.Type.BEFORE);
        }
        getCompoundID().replace(compoundIDFromText);
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

    public Condition<G> getCondition() {
        return Condition.TRUE;
    }

    protected Finalizer<G> getFinalizer() {
        return Finalizer.EMPTY;
    }

    public Condition<G> getFullCondition() {
        return getCondition();
    }

    @Override
    public LSFResolveResult resolveNoCache() {
        //noinspection RedundantTypeArguments - отказывается компилироваться с language level 8
        Collection<G> decls = LSFGlobalResolver.<FullNameStubElement, G>findElements(getNameRef(), getFullNameRef(), getLSFFile(), getStubElementTypes(), getCondition(), getFinalizer());
        return new LSFResolveResult(decls, resolveDefaultErrorAnnotator(decls));
    }
}
