package com.lsfusion.lang.typeinfer;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFMetaCodeStatement;
import com.lsfusion.lang.psi.LSFResolver;
import com.lsfusion.lang.psi.context.ContextModifier;
import com.lsfusion.lang.psi.context.ExtendParamContext;
import com.lsfusion.lang.psi.context.ModifyParamContext;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;
import com.lsfusion.lang.psi.declarations.impl.LSFActionOrGlobalPropDeclarationImpl;
import com.lsfusion.util.BaseUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypeInferer {

    private static void recTypeInfer(PsiElement element, Set<LSFExprParamDeclaration> currentParams, MetaTransaction transaction, boolean first) {
        if (!first && element instanceof LSFMetaCodeStatement)
            return;

        if (element instanceof ModifyParamContext) {
            if (!(element instanceof ExtendParamContext))
                currentParams = new HashSet<>();

            ModifyParamContext modifyContext = (ModifyParamContext) element;
            ContextModifier contextModifier = modifyContext.getContextModifier();
            Set<LSFExprParamDeclaration> params = new HashSet<>(contextModifier.resolveParams(Integer.MAX_VALUE, currentParams));
            InferResult inferred = modifyContext.getContextInferrer().inferClasses(params).finish();
            for (LSFExprParamDeclaration param : params) {
                LSFClassSet inferredClass = inferred.get(param);
                if (inferredClass != null && param instanceof LSFParamDeclaration) {
                    ((LSFParamDeclaration) param).ensureClass(inferredClass.getCommonClass(), transaction);
                }
            }
            currentParams = BaseUtils.merge(currentParams, params);
        }

        for (PsiElement child : element.getChildren())
            recTypeInfer(child, currentParams, transaction, false);
    }

    public static void typeInfer(PsiElement modifyContext, MetaTransaction metaTrans) {
        assert !(modifyContext instanceof ExtendParamContext);
        recTypeInfer(modifyContext, new HashSet<>(), metaTrans, true);
    }

    public static void metaTypeInfer(LSFMetaDeclaration metaDecl, MetaTransaction transaction) {
        List<LSFMetaCodeStatement> usages = LSFResolver.findMetaUsages(metaDecl);
        for (LSFMetaCodeStatement usage : usages)
            typeInfer(usage, transaction);
    }

    public static void typeInfer(LSFFile file, MetaTransaction transaction) {
        for(PsiElement child : file.getStatements())
            if(child instanceof LSFMetaDeclaration)
                metaTypeInfer((LSFMetaDeclaration)child, transaction);
            else
                typeInfer(child, transaction);
    }

    private static void recFindNotInferred(PsiElement element, Set<LSFExprParamDeclaration> currentParams, LSFMetaCodeStatement statement) {
        if (element instanceof ModifyParamContext) {
            if (!(element instanceof ExtendParamContext))
                currentParams = new HashSet<>();

            boolean unfr = false;
            ModifyParamContext modifyContext = (ModifyParamContext) element;
            if (element instanceof LSFActionOrGlobalPropDeclarationImpl) {
                LSFActionOrGlobalPropDeclarationImpl ps = (LSFActionOrGlobalPropDeclarationImpl) element;
                if (ps.isUnfriendly())
                    unfr = true;
            }

            ContextModifier contextModifier = modifyContext.getContextModifier();
            Set<LSFExprParamDeclaration> params = new HashSet<>(contextModifier.resolveParams(Integer.MAX_VALUE, currentParams));
            if (!unfr) {
                for (LSFExprParamDeclaration param : params) {
                    LSFClassSet resClass = param.resolveClass();
                    if (resClass == null) {
                        //                    Notifications.Bus.notify(new Notificati(on("unResolved", "Unresolved", element.getContainingFile().getName() + " " + element.getTextRange(), NotificationType.INFORMATION));

                        System.out.println(element.getContainingFile().getName() + " " + (statement == null ? "NO" : statement.getMetaCodeStatementHeader().getMetacodeUsage().getText()) + " " + element.getText());
                        break;
                    }
                }
            }
            currentParams = BaseUtils.merge(currentParams, params);
        }

        for (PsiElement child : element.getChildren())
            recFindNotInferred(child, currentParams, element instanceof LSFMetaCodeStatement ? (LSFMetaCodeStatement) element : statement);
    }

    public static void findNotInferred(LSFFile file) {
        recFindNotInferred(file, new HashSet<>(), null);
    }

}
