package com.simpleplugin.typeinfer;

import com.intellij.psi.PsiElement;
import com.simpleplugin.BaseUtils;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.meta.MetaTransaction;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.context.ContextModifier;
import com.simpleplugin.psi.context.ExtendParamContext;
import com.simpleplugin.psi.context.ModifyParamContext;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;
import com.simpleplugin.psi.declarations.LSFMetaDeclaration;
import com.simpleplugin.psi.declarations.LSFModuleDeclaration;
import com.simpleplugin.psi.declarations.LSFParamDeclaration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypeInferer {
    
    private static void recTypeInfer(PsiElement element, Set<LSFExprParamDeclaration> currentParams, MetaTransaction transaction, boolean first) {
        if(!first && element instanceof LSFMetaCodeStatement)
            return;
        
        if(element instanceof ModifyParamContext) {
            if(!(element instanceof ExtendParamContext))
                currentParams = new HashSet<LSFExprParamDeclaration>();

            ModifyParamContext modifyContext = (ModifyParamContext)element;
            ContextModifier contextModifier = modifyContext.getContextModifier();
            Set<LSFExprParamDeclaration> params = new HashSet<LSFExprParamDeclaration>(contextModifier.resolveParams(Integer.MAX_VALUE, currentParams));
            InferResult inferred = modifyContext.getContextInferrer().inferClasses(params).finish();
            for(LSFExprParamDeclaration param : params) {
                LSFClassSet inferredClass = inferred.get(param);
                if(inferredClass != null && param instanceof LSFParamDeclaration) {
                    ((LSFParamDeclaration)param).ensureClass(inferredClass.getCommonClass(), transaction);
                }
            }
            currentParams = BaseUtils.merge(currentParams, params);
        }

        for(PsiElement child : element.getChildren())
            recTypeInfer(child, currentParams, transaction, false);
    }
    
    public static void typeInfer(PsiElement modifyContext, MetaTransaction metaTrans) {
        assert !(modifyContext instanceof ExtendParamContext);
        recTypeInfer(modifyContext, new HashSet<LSFExprParamDeclaration>(), metaTrans, true);
    }

    public static void metaTypeInfer(LSFMetaDeclaration metaDecl, MetaTransaction transaction) {
        List<LSFMetaCodeStatement> usages = LSFResolver.findMetaUsages(metaDecl);
        for(LSFMetaCodeStatement usage : usages)
            typeInfer(usage, transaction);
    }
    
    public static void typeInfer(LSFFile file, MetaTransaction transaction) {
        for(PsiElement child : file.getChildren())
            if(child instanceof LSFMetaDeclaration)
                metaTypeInfer((LSFMetaDeclaration)child, transaction);
            else
                typeInfer(child, transaction);        
    }
}
