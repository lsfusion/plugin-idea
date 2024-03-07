package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.cache.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.references.LSFActionOrPropReference;
import com.lsfusion.lang.psi.references.LSFPropReference;
import com.lsfusion.lang.psi.stubs.StatementPropStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class LSFStatementGlobalPropDeclarationImpl extends LSFActionOrGlobalPropDeclarationImpl<LSFStatementGlobalPropDeclaration, StatementPropStubElement> implements LSFStatementGlobalPropDeclaration {

    public LSFStatementGlobalPropDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFStatementGlobalPropDeclarationImpl(@NotNull StatementPropStubElement propStubElement, @NotNull IStubElementType nodeType) {
        super(propStubElement, nodeType);
    }

    public abstract LSFPropertyDeclaration getPropertyDeclaration();

    @Nullable
    public abstract LSFPropertyCalcStatement getPropertyCalcStatement();

    @Override
    public boolean isAbstract() {
        LSFPropertyCalcStatement pCalcStatement = getPropertyCalcStatement();
        if(pCalcStatement != null) {
            LSFExpressionUnfriendlyPD unfriend = pCalcStatement.getExpressionUnfriendlyPD();
            if (unfriend != null) {
                return unfriend.getAbstractPropertyDefinition() != null;
            }
        }
        return false;
    }

    @Override
    public boolean isUnfriendly() {
        LSFPropertyCalcStatement pCalcStatement = getPropertyCalcStatement();
        if(pCalcStatement != null)
            return pCalcStatement.getExpressionUnfriendlyPD() != null;
        return false;
    }

    @Override
    public LSFExClassSet resolveExValueClassNoCache(boolean infer) {
        LSFPropertyCalcStatement pCalcStatement = getPropertyCalcStatement();
        if(pCalcStatement != null) {
            LSFExpressionUnfriendlyPD unfr = pCalcStatement.getExpressionUnfriendlyPD();
            if (unfr != null)
                return unfr.resolveUnfriendValueClass(infer);

            LSFPropertyExpression expr = pCalcStatement.getPropertyExpression();
            if (expr != null)
                return expr.resolveValueClass(infer);
        }

        return null;
    }

    @Nullable
    protected List<LSFExClassSet> resolveValueParamClasses(List<LSFParamDeclaration> declareParams) {
        LSFPropertyCalcStatement pCalcStatement = getPropertyCalcStatement();
        if(pCalcStatement != null) {
            LSFExpressionUnfriendlyPD unfr = pCalcStatement.getExpressionUnfriendlyPD();
            if (unfr != null)
                return unfr.resolveValueParamClasses(declareParams);

            LSFPropertyExpression expr = pCalcStatement.getPropertyExpression();
            if (expr != null)
                return LSFExClassSet.toEx(LSFPsiImplUtil.resolveValueParamClasses(expr, declareParams));
        }
        return null;
    }
    
    public InferExResult inferExParamClasses(LSFExClassSet valueClass, List<LSFExClassSet> resultClasses, Result<List<LSFExprParamDeclaration>> rParams) {
        InferExResult inferredClasses = null;
        LSFPropertyCalcStatement pCalcStatement = getPropertyCalcStatement();
        if(pCalcStatement != null) {
            LSFExpressionUnfriendlyPD unfriendlyPD = pCalcStatement.getExpressionUnfriendlyPD();
            if (unfriendlyPD != null) {
                PsiElement element = unfriendlyPD.getChildren()[0]; // лень создавать отдельный параметр или интерфейс
                if (element instanceof LSFGroupPropertyDefinition) {
                    List<LSFExClassSet> inferredValueClasses = LSFPsiImplUtil.inferGroupValueParamClasses((LSFGroupPropertyDefinition) element);
                    for (int i = 0; i < resultClasses.size(); i++)
                        if (resultClasses.get(i) == null && i < inferredValueClasses.size()) { // не определены, возьмем выведенные
                            resultClasses.set(i, inferredValueClasses.get(i));
                        }
                }
            } else {
                LSFPropertyExpression expr = pCalcStatement.getPropertyExpression();
                assert expr != null;
                if (rParams.getResult() == null)
                    rParams.setResult(expr.resolveParams());

                inferredClasses = LSFPsiImplUtil.inferParamClasses(expr, valueClass).finishEx();
            }
        }
        return inferredClasses;
    }

    public boolean isAction() {
        return false;
    }

    public LSFDataPropertyDefinition getDataPropertyDefinition() {
        LSFPropertyCalcStatement pCalcStatement = getPropertyCalcStatement();
        if(pCalcStatement != null) {
            LSFExpressionUnfriendlyPD expressionUnfriendlyPD = pCalcStatement.getExpressionUnfriendlyPD();
            if (expressionUnfriendlyPD != null) {
                return expressionUnfriendlyPD.getDataPropertyDefinition();
            }
        }
        return null;
    }

    public boolean isDataProperty() {
        LSFDataPropertyDefinition dataProp = getDataPropertyDefinition();
        return dataProp != null;
    }
    
    public boolean isDataStoredProperty() {
        LSFDataPropertyDefinition dataProp = getDataPropertyDefinition();
        return dataProp != null && dataProp.getDataPropertySessionModifier() == null;
    }

    public boolean isMaterializedProperty() {
        LSFNonEmptyPropertyOptions options = getNonEmptyPropertyOptions();
        return options != null && !options.getMaterializedSettingList().isEmpty();
    }
    
    public boolean isStoredProperty() {
        return isDataStoredProperty() || isMaterializedProperty();
    }

    @NotNull
    public String getValuePresentableText() {
        String valueClassString;
        LSFClassSet valueClass = resolveValueClass();
        valueClassString = ": " + (valueClass == null ? "?" : valueClass);
        return valueClassString;
    }

    protected LSFExplicitClasses getImplicitExplicitParams() {
        LSFPropertyCalcStatement pCalcStatement = getPropertyCalcStatement();
        if(pCalcStatement != null) {
            LSFPropertyExpression pExpression = pCalcStatement.getPropertyExpression();
            if (pExpression != null)
                return LSFPsiImplUtil.getClassNameRefs(BaseUtils.<List<LSFParamDeclaration>>immutableCast(pExpression.resolveParams()));

            LSFExpressionUnfriendlyPD unfriend = pCalcStatement.getExpressionUnfriendlyPD();
            if(unfriend != null)
                return LSFPsiImplUtil.getValueParamClassNames(unfriend);
        }
        return null;
    }

    public Set<String> getExplicitValues() {
        LSFPropertyCalcStatement pCalcStatement = getPropertyCalcStatement();
        if(pCalcStatement != null) {
            LSFPropertyExpression pExpression = pCalcStatement.getPropertyExpression();
            if (pExpression != null)
                return LSFImplicitExplicitClasses.getNotNullSet(pExpression.getValueClassNames());

            LSFExpressionUnfriendlyPD unfriend = pCalcStatement.getExpressionUnfriendlyPD();
            if(unfriend != null)
                return LSFImplicitExplicitClasses.getNotNullSet(LSFPsiImplUtil.getValueClassNames(unfriend));
        }

        return null;
    }

    @Override
    public Collection<FullNameStubElementType> getTypes() {
        return Arrays.asList(LSFStubElementTypes.STATEMENTPROP, LSFStubElementTypes.AGGRPARAMPROP);
    }

    @Override
    protected LSFPropReference getImplementation(PsiReference ref) {
        LSFOverridePropertyStatement overrideStatement = PsiTreeUtil.getParentOfType((PsiElement) ref, LSFOverridePropertyStatement.class);
        if (overrideStatement != null && ref.equals(overrideStatement.getMappedPropertyClassParamDeclare().getPropertyUsageWrapper().getPropertyUsage()))
           return overrideStatement.getMappedPropertyClassParamDeclare().getPropertyUsageWrapper().getPropertyUsage();
        return null;
    }

    public static Integer getPropComplexity(LSFPropDeclaration prop) {
        return getPropComplexity(prop, new HashSet<>());
    }
    
    private static Integer getPropComplexity(LSFPropDeclaration prop, Set<LSFPropDeclaration> processed) {
        Integer complexity = 1;
        if (!processed.contains(prop)) {
            processed.add(prop);
            if (prop instanceof LSFGlobalPropDeclaration && !((LSFGlobalPropDeclaration<?, ?>) prop).isMaterializedProperty()) {
                Set<LSFActionOrGlobalPropDeclaration<?, ?>> dependencies = PropertyDependenciesCache.getInstance(prop.getProject()).resolveWithCaching((LSFGlobalPropDeclaration<?, ?>) prop);
                if(dependencies != null) {
                    for (LSFActionOrGlobalPropDeclaration<?, ?> dependency : dependencies) {
                        complexity += getPropComplexity((LSFGlobalPropDeclaration<?, ?>) dependency, processed);
                    }
                }
            }
        }

        return complexity;    
    }

    @Override
    protected List<LSFExprParamDeclaration> resolveValueParamNames() {
        LSFPropertyCalcStatement pCalcStatement = getPropertyCalcStatement();
        if(pCalcStatement != null) {
            LSFPropertyExpression pe = pCalcStatement.getPropertyExpression();
            if (pe != null)
                return LSFPsiImplUtil.resolveParams(pe);
        }
        return null;
    }

    @Override
    protected PsiElement getDependenciesBody() {
        return getPropertyCalcStatement();
    }

    @Override
    protected void fillImplementationDependencies(LSFActionOrPropReference<?, ?> impRef, Collection<LSFActionOrPropReference> references) {
        LSFOverridePropertyStatement overrideStatement = PsiTreeUtil.getParentOfType(impRef, LSFOverridePropertyStatement.class);
        if(overrideStatement != null) {
            for (LSFPropertyExpression propertyExpression : overrideStatement.getPropertyExpressionList()) {
                references.addAll(PsiTreeUtil.findChildrenOfType(propertyExpression, LSFPropReference.class));
            }
        }
    }
}
