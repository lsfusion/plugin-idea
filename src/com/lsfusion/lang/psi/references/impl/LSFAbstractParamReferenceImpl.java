package com.lsfusion.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.psi.LSFResolveResult;
import com.lsfusion.classes.LSFClassSet;
import com.lsfusion.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.psi.references.LSFAbstractParamReference;
import com.lsfusion.psi.references.LSFObjectReference;
import com.lsfusion.typeinfer.InferResult;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class LSFAbstractParamReferenceImpl<T extends LSFExprParamDeclaration> extends LSFReferenceImpl<T> implements LSFAbstractParamReference<T> {

    public LSFAbstractParamReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isSoft() {
        return false;
    }

    protected PsiElement getParamDeclare() {
        return this;
    }

    private Set<LSFExprParamDeclaration> getContextParams() {
        PsiElement paramDecl = getParamDeclare();
        return LSFPsiUtils.getContextParams(paramDecl, this instanceof LSFObjectReference);
    }

    @Nullable
    @Override
    public LSFClassSet resolveClass() {
        T decl = resolveDecl();
        if(decl == null)
            return null;
        return decl.resolveClass();
    }

    @Nullable
    @Override
    public LSFClassSet resolveInferredClass(@Nullable InferResult inferred) {
        T decl = resolveDecl();
        if(decl == null)
            return null;
        LSFClassSet result = decl.resolveClass();
        if(result == null && inferred != null)
            result = inferred.get(decl);
        return result;
    }

    @Override
    public LSFResolveResult resolveNoCache() {
        final List<T> objects = new ArrayList<T>();
        if (getSimpleName() != null) {
            final String nameRef = getNameRef();
            for (LSFExprParamDeclaration decl : getContextParams()) {
                if (decl.getDeclName().equals(nameRef)) {
                    objects.add((T) decl);
                }
            }
        }
        return new LSFResolveResult(objects, resolveDefaultErrorAnnotator(objects));
    }
}
