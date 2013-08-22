package com.simpleplugin;

import com.intellij.psi.PsiElement;
import com.simpleplugin.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LSFPsiImplUtil {

    public static List<PsiElement> getContextModifier(@NotNull LSFPropertyStatement sourceStatement) {
        LSFParamDeclareList decl = sourceStatement.getPropertyDeclaration().getParamDeclareList();
        if(decl!=null)
            return Collections.<PsiElement>singletonList(decl);
        LSFPropertyExpression expression = sourceStatement.getPropertyExpression();
        if(expression!=null)
            return Collections.<PsiElement>singletonList(expression);

        return new ArrayList<PsiElement>();        
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFOverrideStatement sourceStatement) {
        LSFParamDeclareList decl = sourceStatement.getMappedPropertyParamDeclare().getParamDeclareList();
        return Collections.<PsiElement>singletonList(decl);
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFActionPropertyDefinition sourceStatement) {
        LSFParamDeclareList decl = sourceStatement.getParamDeclareList();
        if(decl!=null)
            return Collections.<PsiElement>singletonList(decl);
        return new ArrayList<PsiElement>();
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFConstraintStatement sourceStatement) {
        LSFPropertyExpression decl = sourceStatement.getPropertyExpression();
        if(decl==null)
            return new ArrayList<PsiElement>();
        return Collections.<PsiElement>singletonList(decl);
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFFollowsStatement sourceStatement) {
        LSFParamDeclareList decl = sourceStatement.getMappedPropertyParamDeclare().getParamDeclareList();
        return Collections.<PsiElement>singletonList(decl);
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFEventStatement sourceStatement) {
        LSFPropertyExpression decl = sourceStatement.getPropertyExpression();
        if(decl==null)
            return new ArrayList<PsiElement>();
        return Collections.<PsiElement>singletonList(decl);
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFGlobalEventStatement sourceStatement) {
        return new ArrayList<PsiElement>();
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFWriteWhenStatement sourceStatement) {
        LSFParamDeclareList decl = sourceStatement.getMappedPropertyParamDeclare().getParamDeclareList();
        return Collections.<PsiElement>singletonList(decl);
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFAspectStatement sourceStatement) {
        LSFMappedPropertyParamDeclare decl = sourceStatement.getMappedPropertyParamDeclare();
        if(decl==null)
            return new ArrayList<PsiElement>();
        return Collections.<PsiElement>singletonList(decl.getParamDeclareList());
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        List<PsiElement> result = new ArrayList<PsiElement>();
        for(LSFNonEmptyPropertyExpressionList exprList : sourceStatement.getNonEmptyPropertyExpressionListList())
            result.addAll(exprList.getPropertyExpressionList());
        return result;
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFPropertyExprObject sourceStatement) {
        LSFPropertyExpression expression = sourceStatement.getPropertyExpression();
        if(expression!=null)
            return Collections.<PsiElement>singletonList(expression); 
        return new ArrayList<PsiElement>();
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFForActionPropertyDefinitionBody sourceStatement) {
        List<PsiElement> result = new ArrayList<PsiElement>();
        LSFPropertyExpression pe = sourceStatement.getPropertyExpression();
        if(pe!=null)
            result.add(pe);
        LSFForAddObjClause addObjClause = sourceStatement.getForAddObjClause();
        if(addObjClause!=null)
            result.add(addObjClause);
        return result;
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFWhileActionPropertyDefinitionBody sourceStatement) {
        return Collections.<PsiElement>singletonList(sourceStatement.getPropertyExpression());
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFAssignActionPropertyDefinitionBody sourceStatement) {
        return Collections.<PsiElement>singletonList(sourceStatement.getMappedPropertyExprParam().getExprParameterUsageList());
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFChangeClassActionPropertyDefinitionBody sourceStatement) {
        return Collections.<PsiElement>singletonList(sourceStatement.getExprParameterUsage());
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFDeleteActionPropertyDefinitionBody sourceStatement) {
        return Collections.<PsiElement>singletonList(sourceStatement.getExprParameterUsage());
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFAddObjectActionPropertyDefinitionBody sourceStatement) {
        LSFPropertyExpression expression = sourceStatement.getPropertyExpression();
        if(expression==null)
            return new ArrayList<PsiElement>();
        return Collections.<PsiElement>singletonList(expression);
    }

}
