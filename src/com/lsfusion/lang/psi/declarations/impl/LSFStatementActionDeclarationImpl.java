package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFStatementActionDeclaration;
import com.lsfusion.lang.psi.references.LSFActionOrPropReference;
import com.lsfusion.lang.psi.references.LSFPropReference;
import com.lsfusion.lang.psi.stubs.StatementActionStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendStubElementType;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class LSFStatementActionDeclarationImpl extends LSFActionOrGlobalPropDeclarationImpl<LSFStatementActionDeclaration, StatementActionStubElement> implements LSFStatementActionDeclaration {
    public LSFStatementActionDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFStatementActionDeclarationImpl(@NotNull StatementActionStubElement actionStubElement, @NotNull IStubElementType nodeType) {
        super(actionStubElement, nodeType);
    }

    @Nullable
    public abstract LSFActionUnfriendlyPD getActionUnfriendlyPD();
    @Nullable
    public abstract LSFListActionPropertyDefinitionBody getListActionPropertyDefinitionBody();


    protected LSFExplicitClasses getImplicitExplicitParams() {
        LSFActionUnfriendlyPD unfriend = getActionUnfriendlyPD();
        if (unfriend != null)
            return LSFPsiImplUtil.getValueParamClassNames(unfriend);
        return null;
    }

    @Override
    public ExtendStubElementType<?, ?> getExtendElementType() {
        return LSFStubElementTypes.EXTENDACTION;
    }

    @Nullable
    protected List<LSFExClassSet> resolveValueParamClasses(List<LSFParamDeclaration> declareParams) {
        return LSFPsiImplUtil.resolveValueParamClasses(getActionUnfriendlyPD(), getListActionPropertyDefinitionBody(), declareParams);
    }

    @Override
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.STATEMENTACTION;
    }

    @Override
    public boolean isAbstract() {
        LSFActionUnfriendlyPD unfriend = getActionUnfriendlyPD();
        if (unfriend != null) {
            return unfriend.getAbstractActionPropertyDefinition() != null;
        }
        return false;
    }

    public boolean isAction() {
        return true;
    }

    @Override
    public boolean isUnfriendly() {
        return getActionUnfriendlyPD() != null;
    }

    @Override
    protected InferExResult inferExParamClasses(LSFExClassSet valueClass, List<LSFExClassSet> resultClasses, Result<List<LSFExprParamDeclaration>> rParams) {
        InferExResult inferredClasses = null;
        if(getListActionPropertyDefinitionBody() != null) {
            if (rParams.getResult() != null) // может быть action unfriendly
                inferredClasses = LSFPsiImplUtil.inferActionParamClasses(getListActionPropertyDefinitionBody(), new HashSet<>(rParams.getResult())).finishEx();
        }
        return inferredClasses;
    }

    @Override
    public String getValuePresentableText() {
        return "";
    }

    @Override
    protected PsiElement getDependenciesBody() {
        return getListActionPropertyDefinitionBody();
    }

    @Override
    protected void fillImplementationDependencies(LSFActionOrPropReference<?, ?> impRef, Collection<LSFActionOrPropReference> references) {
        LSFOverrideActionStatement overrideStatement = PsiTreeUtil.getParentOfType(impRef, LSFOverrideActionStatement.class);
        if(overrideStatement != null) {
            LSFPropertyExpression pe = overrideStatement.getPropertyExpression();
            if (pe != null)
                references.addAll(PsiTreeUtil.findChildrenOfType(pe, LSFPropReference.class));
            LSFListActionPropertyDefinitionBody body = overrideStatement.getListActionPropertyDefinitionBody();
            references.addAll(PsiTreeUtil.findChildrenOfType(body, LSFActionOrPropReference.class));
        }
    }

    @Override
    public @Nullable LSFNonEmptyPropertyOptions getNonEmptyPropertyOptions() {
        return null;
    }

    public boolean hasExplicitReturn() {
        LSFActionUnfriendlyPD actionUnfriendlyPD = getActionUnfriendlyPD();
        if (actionUnfriendlyPD != null) {
            LSFAbstractActionPropertyDefinition abstractAction = actionUnfriendlyPD.getAbstractActionPropertyDefinition();
            return abstractAction != null && abstractAction.getAbstractReturn() != null;
        }
        return !getReturnStatements().isEmpty();
    }

    @Override
    public LSFExClassSet resolveExValueClassNoCache(boolean infer) {
        LSFActionUnfriendlyPD actionUnfriendlyPD = getActionUnfriendlyPD();
        if(actionUnfriendlyPD != null) {
            LSFAbstractActionPropertyDefinition abstractAction = actionUnfriendlyPD.getAbstractActionPropertyDefinition();
            if(abstractAction != null) {
                LSFAbstractReturn abstractReturn = abstractAction.getAbstractReturn();
                if(abstractReturn != null) {
                    LSFClassName className = abstractReturn.getClassName();
                    LSFClassSet classSet = LSFPsiImplUtil.resolveClass(className);
                    if (classSet != null)
                        return LSFExClassSet.toEx(classSet);
                }
            }
        } else {
            List<LSFExClassSet> returnValueClasses = new ArrayList<>();
            for (LSFReturnActionPropertyDefinitionBody returnChild : getReturnStatements()) {
                LSFExClassSet dependClass = returnChild.getPropertyExpression().resolveValueClass(infer);
                if (dependClass != null)
                    returnValueClasses.add(dependClass);
            }
            return LSFPsiImplUtil.orClasses(returnValueClasses, false);
        }
        return null;
    }

    private Collection<LSFReturnActionPropertyDefinitionBody> getReturnStatements() {
        return PsiTreeUtil.findChildrenOfType(getListActionPropertyDefinitionBody(), LSFReturnActionPropertyDefinitionBody.class);
    }

    @Override
    public List<LSFExClassSet> resolveExParamClassesNoCache(boolean joinAction) {
        List<LSFExClassSet> params = super.resolveExParamClassesNoCache(joinAction);
        if(!joinAction || params == null)
            return params;

        List<LSFExClassSet> result = new ArrayList<>();
        result.addAll(params);

        LSFActionUnfriendlyPD actionUnfriendlyPD = getActionUnfriendlyPD();
        if(actionUnfriendlyPD != null) {
            LSFAbstractActionPropertyDefinition abstractAction = actionUnfriendlyPD.getAbstractActionPropertyDefinition();
            if(abstractAction != null) {
                LSFAbstractReturn abstractReturn = abstractAction.getAbstractReturn();
                if(abstractReturn != null)
                    result.addAll(LSFExClassSet.toEx(LSFPsiImplUtil.resolveClasses(abstractReturn.getClassNameList())));
            }
        } else {
            List<List<LSFExClassSet>> resultInterfaceClasses = new ArrayList<>();
            for (LSFReturnActionPropertyDefinitionBody returnChild : getReturnStatements()) {
                Set<LSFExprParamDeclaration> upParams = LSFPsiUtils.getContextParams(returnChild, LSFLocalSearchScope.GLOBAL, false, false);
                List<LSFExprParamDeclaration> returnParams = LSFPsiUtils.resolveParams(returnChild.getPropertyExpression(), upParams);

                resultInterfaceClasses.add(LSFExClassSet.toEx(LSFPsiImplUtil.resolveParamDeclClasses(returnParams)));
            }
            List<LSFExClassSet> merged = LSFPsiImplUtil.orClassesList(resultInterfaceClasses, false);
            if (merged != null)
                result.addAll(merged);
        }
        return result;
    }
}
