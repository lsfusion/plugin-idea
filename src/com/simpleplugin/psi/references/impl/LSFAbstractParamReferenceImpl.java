package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.simpleplugin.BaseUtils;
import com.simpleplugin.LSFDeclarationResolveResult;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.LSFFile;
import com.simpleplugin.psi.context.ContextModifier;
import com.simpleplugin.psi.context.ExtendParamContext;
import com.simpleplugin.psi.context.ModifyParamContext;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;
import com.simpleplugin.psi.declarations.LSFObjectDeclaration;
import com.simpleplugin.psi.extend.LSFFormExtend;
import com.simpleplugin.psi.references.LSFAbstractParamReference;
import com.simpleplugin.psi.references.LSFObjectReference;
import com.simpleplugin.typeinfer.InferResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class LSFAbstractParamReferenceImpl<T extends LSFExprParamDeclaration> extends LSFReferenceImpl<T> implements LSFAbstractParamReference<T> {

    public LSFAbstractParamReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isSoft() {
        return false;
    }

    private static Set<LSFExprParamDeclaration> processParams(PsiElement current, int offset, boolean objectRef) {
        if(current instanceof ModifyParamContext) {
            ModifyParamContext extendElement = (ModifyParamContext) current;
            ContextModifier contextModifier = extendElement.getContextModifier();

            Set<LSFExprParamDeclaration> upParams;
            Set<LSFExprParamDeclaration> result = new HashSet<LSFExprParamDeclaration>();
            if(current instanceof ExtendParamContext) {
                upParams = processParams(current.getParent(), offset, objectRef);
                result.addAll(upParams);
            } else { // не extend - останавливаемся
                upParams = new HashSet<LSFExprParamDeclaration>();
            }
            result.addAll(contextModifier.resolveParams(offset, upParams));
            return result;
        } else {
            Set<LSFObjectDeclaration> objects = LSFFormElementReferenceImpl.processFormContext(current, new LSFFormElementReferenceImpl.Processor<LSFObjectDeclaration>() {
                public Collection<LSFObjectDeclaration> process(LSFFormExtend formExtend) {
                    return formExtend.getObjectDecls();
                }
            }, objectRef);
            if(objects!=null)
                return BaseUtils.<LSFExprParamDeclaration, LSFObjectDeclaration>immutableCast(objects);
        }

        PsiElement parent = current.getParent();
        if(!(parent == null || parent instanceof LSFFile))
            return processParams(parent, offset, objectRef); // бежим выше
        
        return new HashSet<LSFExprParamDeclaration>();
    }
    
    protected PsiElement getParamDeclare() {
        return this;
    }

    private Set<LSFExprParamDeclaration> processParams() {
        PsiElement paramDecl = getParamDeclare();
        return processParams(paramDecl, paramDecl.getTextOffset(), this instanceof LSFObjectReference);
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
    public LSFDeclarationResolveResult resolveNoCache() {
        final List<T> objects = new ArrayList<T>();
            if(getSimpleName() != null) {
            
            final String nameRef = getNameRef();
            for(LSFExprParamDeclaration decl : processParams())
                if(decl.getDeclName().equals(nameRef))
                    objects.add((T) decl);
        }
        return new LSFDeclarationResolveResult(objects, resolveDefaultErrorAnnotator(objects));
    }

    @Override
    protected void fillListVariants(final Collection<String> variants) {
        for(LSFExprParamDeclaration decl : processParams())
            variants.add(decl.getDeclName());
    }
}
