package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.LSFResolveResult;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.psi.references.LSFAbstractParamReference;
import com.lsfusion.lang.psi.references.LSFObjectReference;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
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
        return LSFPsiUtils.getContextParams(paramDecl, this instanceof LSFObjectReference, false);
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
    public LSFExClassSet resolveInferredClass(@Nullable InferExResult inferred) {
        T decl = resolveDecl();
        if(decl == null)
            return null;
        LSFExClassSet result = LSFExClassSet.toEx(decl.resolveClass());
        if(result == null && inferred != null)
            result = inferred.get(decl);
        return result;
    }

    @Override
    public LSFResolveResult resolveNoCache() {
        final List<T> objects = new ArrayList<>();
        if (getSimpleName() != null) {
            final String nameRef = getNameRef();
            for (LSFExprParamDeclaration decl : getContextParams()) {
                if (decl.getDeclName().equals(nameRef)) {
                    objects.add((T) decl);
                }
            }
        }
        return new LSFResolveResult(objects, resolveDefaultErrorAnnotator(objects, true));
    }
}
