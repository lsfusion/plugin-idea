package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.find.findUsages.DefaultFindUsagesHandlerFactory;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.stubs.PropStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.lang.typeinfer.InferResult;
import com.lsfusion.util.BaseUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

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
    @Nullable
    public LSFId getNameIdentifier() {
        return getPropertyDeclaration().getSimpleNameWithCaption().getSimpleName();
    }

    @Override
    public String getCaption() {
        LSFStringLiteral stringLiteral = getPropertyDeclaration().getSimpleNameWithCaption().getStringLiteral();
        return stringLiteral != null ? stringLiteral.getValue() : null;
    }

    @Override
    public LSFClassSet resolveValueClass(boolean infer) {
        return LSFResolveCache.getValueClassInstance().resolveWithCaching(this, infer ? LSFValueClassResolver.INFER_INSTANCE : LSFValueClassResolver.NO_INFER_INSTANCE, true, false);
    }

    @Override
    public LSFClassSet resolveValueClassNoCache(boolean infer) {
        LSFExpressionUnfriendlyPD unfr = getExpressionUnfriendlyPD();
        if (unfr != null)
            return unfr.resolveUnfriendValueClass(infer);

        LSFPropertyExpression expr = getPropertyExpression();
        if (expr != null)
            return expr.resolveValueClass(infer);

        return null;
    }

    @Nullable
    private List<LSFClassSet> resolveValueParamClasses() {
        LSFExpressionUnfriendlyPD unfr = getExpressionUnfriendlyPD();
        if (unfr != null)
            return unfr.resolveValueParamClasses();

        LSFPropertyExpression expr = getPropertyExpression();
        if (expr != null)
            return LSFPsiImplUtil.resolveValueParamClasses(expr);

        return null;

    }

    @Override
    @Nullable
    public List<LSFClassSet> resolveParamClasses() {
        return LSFResolveCache.getParamClassesInstance().resolveWithCaching(this, LSFParamClassesResolver.INSTANCE, true, false);
    }

    @Override
    public List<LSFClassSet> resolveParamClassesNoCache() {
        LSFPropertyDeclaration decl = getPropertyDeclaration();
        LSFClassParamDeclareList cpd = decl.getClassParamDeclareList();
        List<LSFClassSet> declareClasses = null;
        if (cpd != null) {
            declareClasses = LSFPsiImplUtil.resolveClass(cpd);
            if (LSFPsiImplUtil.allClassesDeclared(declareClasses)) // оптимизация
                return declareClasses;
        }

        List<LSFClassSet> valueClasses = resolveValueParamClasses();
        if (valueClasses == null)
            return declareClasses;

        if (declareClasses == null)
            return valueClasses;

        List<LSFClassSet> mixed = new ArrayList<LSFClassSet>(declareClasses);
        for (int i = 0, size = declareClasses.size(); i < size; i++) {
            if (i >= valueClasses.size())
                break;

            if (declareClasses.get(i) == null)
                mixed.set(i, valueClasses.get(i));
        }
        return mixed;
    }

    @Override
    @Nullable
    public List<LSFClassSet> inferParamClasses(LSFClassSet valueClass) {

        List<LSFClassSet> resultClasses = resolveParamClasses();
        if (resultClasses == null)
            return null;

        //        LSFActionPropertyDefinition action = sourceStatement.getActionPropertyDefinition();
//        return 

        List<LSFExprParamDeclaration> params = null;
        LSFPropertyDeclaration decl = getPropertyDeclaration();
        LSFClassParamDeclareList cpd = decl.getClassParamDeclareList();
        if (cpd != null)
            params = BaseUtils.<List<LSFExprParamDeclaration>>immutableCast(LSFPsiImplUtil.resolveParams(cpd));

        InferResult inferredClasses = null;
        LSFExpressionUnfriendlyPD unfriendlyPD = getExpressionUnfriendlyPD();
        if (unfriendlyPD != null) {
            LSFActionPropertyDefinition actionDef = unfriendlyPD.getActionPropertyDefinition();
            if (actionDef != null) {
                if (params == null)
                    params = BaseUtils.<List<LSFExprParamDeclaration>>immutableCast(LSFPsiImplUtil.resolveParams(actionDef.getExprParameterUsageList()));
                if (params != null) // может быть action unfriendly
                    inferredClasses = LSFPsiImplUtil.inferActionParamClasses(actionDef.getActionPropertyDefinitionBody(), new HashSet<LSFExprParamDeclaration>(params)).finish();
            } else {
                LSFContextIndependentPD contextIndependentPD = unfriendlyPD.getContextIndependentPD();

                assert contextIndependentPD != null;

                PsiElement element = contextIndependentPD.getChildren()[0]; // лень создавать отдельный параметр или интерфейс
                if (element instanceof LSFGroupPropertyDefinition) {
                    List<LSFClassSet> inferredValueClasses = LSFPsiImplUtil.inferValueParamClasses((LSFGroupPropertyDefinition) element);
                    for (int i = 0; i < resultClasses.size(); i++)
                        if (resultClasses.get(i) == null && i < inferredValueClasses.size()) { // не определены, возьмем выведенные
                            resultClasses.set(i, inferredValueClasses.get(i));
                        }
                    return resultClasses;
                }
            }
        } else {
            LSFPropertyExpression expr = getPropertyExpression();
            if (expr != null) {
                if (params == null)
                    params = expr.resolveParams();

                inferredClasses = LSFPsiImplUtil.inferParamClasses(expr, valueClass).finish();
            }
        }
        if (inferredClasses != null) {
            assert resultClasses.size() == params.size();
            for (int i = 0; i < resultClasses.size(); i++)
                if (resultClasses.get(i) == null) { // не определены, возьмем выведенные
                    resultClasses.set(i, inferredClasses.get(params.get(i)));
                }
        }

        return resultClasses;
    }

    public boolean isAbstract() {
        LSFExpressionUnfriendlyPD expressionUnfriendlyPD = getExpressionUnfriendlyPD();
        if (expressionUnfriendlyPD != null) {
            LSFContextIndependentPD contextIndependentPD = expressionUnfriendlyPD.getContextIndependentPD();
            if (contextIndependentPD != null) {
                if (contextIndependentPD.getAbstractPropertyDefinition() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAction() {
        LSFExpressionUnfriendlyPD expressionUnfriendlyPD = getExpressionUnfriendlyPD();
        if (expressionUnfriendlyPD != null) {
            LSFContextIndependentPD contextIndependentPD = expressionUnfriendlyPD.getContextIndependentPD();
            if (contextIndependentPD != null) {
                if (contextIndependentPD.getAbstractActionPropertyDefinition() != null) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean isDataProperty() {
        LSFExpressionUnfriendlyPD expressionUnfriendlyPD = getExpressionUnfriendlyPD();
        if (expressionUnfriendlyPD != null) {
            LSFContextIndependentPD contextIndependentPD = expressionUnfriendlyPD.getContextIndependentPD();
            if (contextIndependentPD != null && contextIndependentPD.getDataPropertyDefinition() != null) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        if (isAction()) {
            return LSFIcons.ACTION;
        } else if (isAbstract()) {
            return LSFIcons.ABSTRACT_PROPERTY;
        } else if (isDataProperty()) {
            return LSFIcons.DATA_PROPERTY;
        } else {
            return LSFIcons.PROPERTY;
        }
    }

    @Override
    public String getPresentableText() {
        return getDeclName() + getParamsPresentableText();
    }

    private String getParamsPresentableText() {
        List<? extends LSFExprParamDeclaration> params = getExplicitParams();

        List<LSFClassSet> paramClasses = resolveParamClasses();
        String paramsString = "";
        if (paramClasses != null) {
            int i = 0;
            for (Iterator<LSFClassSet> iterator = paramClasses.iterator(); iterator.hasNext(); ) {
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

        return "(" + paramsString + ")";
    }

    @Override
    public String getSignaturePresentableText() {
        String valueClassString = "";
        if (!isAction()) {
            LSFClassSet valueClass = resolveValueClass(false);
            valueClassString = ": " + (valueClass == null ? "?" : valueClass);
        }

        return getParamsPresentableText() + valueClassString;
    }

    public List<? extends LSFExprParamDeclaration> getExplicitParams() {
        List<? extends LSFExprParamDeclaration> params = getPropertyDeclaration().resolveParamDecls();
        LSFPropertyExpression pExpression = getPropertyExpression();
        if (params == null && pExpression != null) {
            params = pExpression.resolveParams();
        }
        return params;
    }

    @Override
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.PROP;
    }

    @Override
    protected Condition<LSFGlobalPropDeclaration> getFindDuplicatesCondition() {
        return new Condition<LSFGlobalPropDeclaration>() {
            @Override
            public boolean value(LSFGlobalPropDeclaration decl) {
                LSFId nameIdentifier = getNameIdentifier();
                LSFId otherNameIdentifier = decl.getNameIdentifier();
                return nameIdentifier != null && otherNameIdentifier != null &&
                        nameIdentifier.getText().equals(otherNameIdentifier.getText()) &&
                        resolveEquals(resolveParamClasses(), decl.resolveParamClasses());
            }
        };
    }

    public static boolean resolveEquals(List<LSFClassSet> list1, List<LSFClassSet> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i) == null) {
                if (list2.get(i) != null) {
                    return false;
                }
            } else {
                if (!list1.get(i).equals(list2.get(i))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public PsiElement[] processImplementationsSearch() {
        DefaultFindUsagesHandlerFactory fact = new DefaultFindUsagesHandlerFactory();
        LSFId nameIdentifier = getNameIdentifier();
        if (nameIdentifier == null) {
            return PsiElement.EMPTY_ARRAY;
        }

        FindUsagesHandler handler = fact.createFindUsagesHandler(nameIdentifier, false);
        if (handler == null) {
            return PsiElement.EMPTY_ARRAY;
        }
        Collection<PsiReference> refs = handler.findReferencesToHighlight(nameIdentifier, GlobalSearchScope.allScope(getProject()));

        List<PsiElement> result = new ArrayList<PsiElement>();
        for (PsiReference ref : refs) {
            if (((PsiElement) ref).getParent().getParent() instanceof LSFOverrideStatement) {
                LSFOverrideStatement override = (LSFOverrideStatement) ((PsiElement) ref).getParent().getParent();
                result.add(override.getMappedPropertyClassParamDeclare().getPropertyUsage());
            }
        }
        return result.toArray(new PsiElement[result.size()]);
    }
}
