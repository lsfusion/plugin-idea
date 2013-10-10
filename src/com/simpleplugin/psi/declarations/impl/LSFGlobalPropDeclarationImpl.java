package com.simpleplugin.psi.declarations.impl;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.BaseUtils;
import com.simpleplugin.LSFPsiImplUtil;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;
import com.simpleplugin.psi.declarations.LSFGlobalPropDeclaration;
import com.simpleplugin.psi.stubs.PropStubElement;
import com.simpleplugin.typeinfer.InferResult;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public abstract class LSFGlobalPropDeclarationImpl extends LSFFullNameDeclarationImpl<LSFGlobalPropDeclaration, PropStubElement> implements LSFGlobalPropDeclaration {

    public LSFGlobalPropDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFGlobalPropDeclarationImpl(@NotNull PropStubElement propStubElement, @NotNull IStubElementType nodeType) {
        super(propStubElement, nodeType);
    }

    protected abstract LSFPropertyDeclaration getPropertyDeclaration();

    @Nullable
    protected abstract LSFExpressionUnfriendlyPD getExpressionUnfriendlyPD();

    @Nullable
    protected abstract LSFPropertyExpression getPropertyExpression();

    @Override
    public LSFId getNameIdentifier() {
        return getPropertyDeclaration().getSimpleNameWithCaption().getSimpleName();
    }

    @Override
    public LSFClassSet resolveValueClass(boolean infer) {
        LSFExpressionUnfriendlyPD unfr = getExpressionUnfriendlyPD();
        if(unfr!=null)
            return unfr.resolveUnfriendValueClass(infer);

        LSFPropertyExpression expr = getPropertyExpression();
        if(expr!=null)
            return expr.resolveValueClass(infer);

        return null; 
    }

    @Nullable
    private List<LSFClassSet> resolveValueParamClasses() {
        LSFExpressionUnfriendlyPD unfr = getExpressionUnfriendlyPD();
        if(unfr!=null)
            return unfr.resolveValueParamClasses();

        LSFPropertyExpression expr = getPropertyExpression();
        if(expr!=null)
            return LSFPsiImplUtil.resolveValueParamClasses(expr);

        return null;

    }

    @Override
    @Nullable
    public List<LSFClassSet> resolveParamClasses() {
        LSFPropertyDeclaration decl = getPropertyDeclaration();
        LSFClassParamDeclareList cpd = decl.getClassParamDeclareList();
        List<LSFClassSet> declareClasses = null;
        if(cpd!=null) {
            declareClasses = LSFPsiImplUtil.resolveClass(cpd);
            if(LSFPsiImplUtil.allClassesDeclared(declareClasses)) // оптимизация
                return declareClasses;
        }

        List<LSFClassSet> valueClasses = resolveValueParamClasses();
        if(valueClasses == null)
            return declareClasses;
            
        if(declareClasses == null)
            return valueClasses;

        List<LSFClassSet> mixed = new ArrayList<LSFClassSet>(declareClasses);
        for(int i=0,size=declareClasses.size();i<size;i++) {
            if(i >= valueClasses.size())
                break;
            
            if(declareClasses.get(i) == null)
                mixed.set(i, valueClasses.get(i));
        }
        return mixed;
    }

    @Override
    @Nullable
    public List<LSFClassSet> inferParamClasses(LSFClassSet valueClass) {

        List<LSFClassSet> resultClasses = resolveParamClasses();
        if(resultClasses == null)
            return null;

        //        LSFActionPropertyDefinition action = sourceStatement.getActionPropertyDefinition();
//        return 

        List<LSFExprParamDeclaration> params = null;
        LSFPropertyDeclaration decl = getPropertyDeclaration();
        LSFClassParamDeclareList cpd = decl.getClassParamDeclareList();
        if(cpd!=null)
            params = BaseUtils.<List<LSFExprParamDeclaration>>immutableCast(LSFPsiImplUtil.resolveParams(cpd));

        InferResult inferredClasses = null;
        LSFExpressionUnfriendlyPD unfriendlyPD = getExpressionUnfriendlyPD();
        if(unfriendlyPD != null) {
            LSFActionPropertyDefinition actionDef = unfriendlyPD.getActionPropertyDefinition();
            if(actionDef != null) {
                if(params == null)
                    params = BaseUtils.<List<LSFExprParamDeclaration>>immutableCast(LSFPsiImplUtil.resolveParams(actionDef.getExprParameterUsageList()));
                if(params != null) // может быть action unfriendly
                    inferredClasses = LSFPsiImplUtil.inferActionParamClasses(actionDef.getActionPropertyDefinitionBody(), new HashSet<LSFExprParamDeclaration>(params)).finish(); 
            } else {
                PsiElement element = unfriendlyPD.getContextIndependentPD().getChildren()[0]; // лень создавать отдельный параметр или интерфейс
                if(element instanceof LSFGroupPropertyDefinition) {
                    List<LSFClassSet> inferredValueClasses = LSFPsiImplUtil.inferValueParamClasses((LSFGroupPropertyDefinition) element);
                    for(int i=0;i<resultClasses.size();i++)
                        if(resultClasses.get(i) == null && i<inferredValueClasses.size()) { // не определены, возьмем выведенные
                            resultClasses.set(i, inferredValueClasses.get(i));
                        }
                    return resultClasses;
                }
            }
        } else {
            LSFPropertyExpression expr = getPropertyExpression();
            if(expr!=null) {
                if(params == null)        
                    params = expr.resolveParams();            
                
                inferredClasses = LSFPsiImplUtil.inferParamClasses(expr, valueClass).finish();
            }
        }
        if(inferredClasses != null) {
            assert resultClasses.size() == params.size();
            for(int i=0;i<resultClasses.size();i++)
                if(resultClasses.get(i) == null) { // не определены, возьмем выведенные
                    resultClasses.set(i, inferredClasses.get(params.get(i)));
                }
        }

        return resultClasses;
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return AllIcons.Nodes.Property;
    }

    @Override
    public String getPresentableDeclText() {
        List<? extends LSFExprParamDeclaration> params = getPropertyDeclaration().resolveParamDecls();
        LSFPropertyExpression pExpression = getPropertyExpression();
        if (params == null && pExpression != null) {
            params = pExpression.resolveParams();
        }

        List<LSFClassSet> paramClasses = resolveParamClasses();
        String paramsString = "";
        if (paramClasses != null) {
            int i = 0;
            for (Iterator<LSFClassSet> iterator = paramClasses.iterator(); iterator.hasNext();) {
                LSFClassSet classSet = iterator.next();
                if (classSet != null) {
                    paramsString += classSet;
                }
                if (params != null && params.get(i) != null) {
                    paramsString += (classSet != null ? " " : "") + params.get(i).getDeclName();
                }
                if (iterator.hasNext()) {
                    paramsString += ", ";
                }
                i++;
            }
        } else if (params != null) {
            paramsString += StringUtils.join(params, ", ");
        }
        
        return getDeclName() + "(" + paramsString + ")";
    }
}
