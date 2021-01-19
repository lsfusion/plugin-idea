package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

// G extends T
public abstract class LSFFullNameReferenceImpl<T extends LSFDeclaration, G extends LSFFullNameDeclaration> extends LSFGlobalReferenceImpl<T> implements LSFFullNameReference<T, G> {

    protected LSFFullNameReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    public static <S extends FullNameStubElement, T extends LSFFullNameDeclaration, SC extends FullNameStubElement<SC, TC>, TC extends LSFFullNameDeclaration<TC, SC>> Collection<T> findElements(LSFFullNameReference ref, Collection<FullNameStubElementType> types, Condition<T> condition, Finalizer<T> finalizer) {
        return findElements(ref, types, condition, finalizer, new ArrayList<>());
    }
    public static <S extends FullNameStubElement, T extends LSFFullNameDeclaration, SC extends FullNameStubElement<SC, TC>, TC extends LSFFullNameDeclaration<TC, SC>> Collection<T> findElements(LSFFullNameReference ref, Collection<FullNameStubElementType> types, Condition<T> condition, Finalizer<T> finalizer, List<T> virtDecls) {
        return LSFGlobalResolver.findElements(ref.getNameRef(), ref.getFullNameRef(), types, ref.getLSFFile(), ref.getOffsetRef(), condition, finalizer, virtDecls);
    }
    public static <S extends FullNameStubElement, T extends LSFFullNameDeclaration, SC extends FullNameStubElement<SC, TC>, TC extends LSFFullNameDeclaration<TC, SC>> Collection<T> findElements(LSFFullNameReferenceImpl refImpl, Condition<T> condition, Finalizer<T> finalizer) {
        return findElements(refImpl, refImpl.getStubElementTypes(), condition, finalizer);
    }

    public static <S extends FullNameStubElement, T extends LSFFullNameDeclaration, SC extends FullNameStubElement<SC, TC>, TC extends LSFFullNameDeclaration<TC, SC>> Collection<T> findNoConditionElements(LSFFullNameReference ref, Collection<FullNameStubElementType> types, Finalizer<T> finalizer) {
        return LSFGlobalResolver.findElements(ref.getNameRef(), ref.getFullNameRef(), types, ref.getLSFFile(), null, Conditions.alwaysTrue(), finalizer);
    }
    public static <S extends FullNameStubElement, T extends LSFFullNameDeclaration, SC extends FullNameStubElement<SC, TC>, TC extends LSFFullNameDeclaration<TC, SC>> Collection<T> findNoConditionElements(LSFFullNameReferenceImpl refImpl, Finalizer<T> finalizer) {
        return findNoConditionElements(refImpl, refImpl.getStubElementTypes(), finalizer);
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
    
    public Integer getOffsetRef() {
        return getTextOffset();
    }

    protected Collection<FullNameStubElementType> getStubElementTypes() {
        return Collections.singleton(getStubElementType());
    }

    protected FullNameStubElementType getStubElementType() {
        return null;
    }

    public Condition<G> getCondition() {
        return Conditions.alwaysTrue();
    }

    protected Finalizer<G> getFinalizer() {
        return Finalizer.EMPTY;
    }

    public Condition<G> getFullCondition() {
        return getCondition();
    }

    protected List<G> getVirtDecls() {
        return new ArrayList<>();
    }
    protected Collection<? extends T> resolveDeclarations() {
        List<G> virtDecls = getVirtDecls();
        Collection decls = findElements(this, getStubElementTypes(), getCondition(), getFinalizer(), virtDecls);
        return (Collection<? extends T>) decls;
    } 

    protected Collection<? extends T> resolveNoConditionDeclarations() {
        return new ArrayList<>();        
    } 

    @Override
    public LSFResolveResult resolveNoCache() {
        Collection<? extends T> declarations = resolveDeclarations();

        LSFResolveResult.ErrorAnnotator errorAnnotator = null;
        if (declarations.size() > 1) {
            errorAnnotator = new LSFResolveResult.AmbigiousErrorAnnotator(this, declarations);
        } else if (declarations.isEmpty()) {
            declarations = resolveNoConditionDeclarations();

            errorAnnotator = new LSFResolveResult.NotFoundErrorAnnotator(this, declarations, false);
        }

        return new LSFResolveResult(declarations, errorAnnotator);
    }
}
