package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.psi.references.LSFExprParamReference;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class LSFExprParamReferenceImpl extends LSFReferenceImpl<LSFExprParamDeclaration> implements LSFExprParamReference {
    
    protected LSFExprParamReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    protected abstract LSFClassParamDeclare getClassParamDeclare();

    protected PsiElement getParamDeclare() {
        return getClassParamDeclare().getParamDeclare();
    }

    @Override
    public LSFId getSimpleName() {
        return getClassParamDeclare().getParamDeclare().getSimpleName();
    }

    @Override
    protected boolean isDeclarationType(PsiElement element) {
        return element instanceof LSFExprParamDeclaration;
    }

    @Override
    public boolean isSoft() {
        return false;
    }

    private Set<LSFExprParamDeclaration> getContextParams() {
        PsiElement paramDecl = getParamDeclare();
        return LSFPsiUtils.getContextParams(paramDecl, LSFLocalSearchScope.createFrom(this), false, false);
    }

    @Nullable
    @Override
    public LSFClassSet resolveClass() {
        LSFExprParamDeclaration decl = resolveDecl();
        if(decl == null)
            return null;
        return decl.resolveClass();
    }

    @Nullable
    @Override
    public LSFExClassSet resolveInferredClass(@Nullable InferExResult inferred) {
        LSFExprParamDeclaration decl = resolveDecl();
        if(decl == null)
            return null;
        LSFExClassSet result = LSFExClassSet.toEx(decl.resolveClass());
        if(result == null && inferred != null)
            result = inferred.get(decl);
        return result;
    }

    @Override
    public LSFResolveResult resolveNoCache() {
        final List<LSFExprParamDeclaration> objects = new ArrayList<>();
        if (getSimpleName() != null) {
            final String nameRef = getNameRef();
            for (LSFExprParamDeclaration decl : getContextParams()) {
                if (decl != null && decl.getDeclName().equals(nameRef)) {
                    objects.add(decl);
                }
            }
        }
        return new LSFResolveResult(objects, resolveDefaultErrorAnnotator(objects, true));
    }
}
