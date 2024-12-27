package com.lsfusion.lang.psi;

import com.intellij.lang.ASTFactory;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.classes.*;
import com.lsfusion.lang.classes.link.*;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.context.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.declarations.impl.LSFActionOrGlobalPropDeclarationImpl;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.extend.impl.LSFFormExtendImpl;
import com.lsfusion.lang.psi.impl.*;
import com.lsfusion.lang.psi.references.LSFAbstractParamReference;
import com.lsfusion.lang.psi.references.LSFActionOrPropReference;
import com.lsfusion.lang.typeinfer.*;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lsfusion.lang.psi.LSFReflectionType.CANONICAL_NAME_VALUE_CLASS;
import static java.util.Collections.singletonList;

@SuppressWarnings("UnusedParameters")
public class LSFPsiImplUtil {

    public static ContextModifier getContextAPModifier(@Nullable LSFPropertyCalcStatement calcStatement) {
        if(calcStatement != null) {
            LSFPropertyExpression expression = calcStatement.getPropertyExpression();
            if (expression != null)
                return new ExprsContextModifier(expression);
            LSFAggrPropertyDefinition aggrDef = calcStatement.getExpressionUnfriendlyPD().getAggrPropertyDefinition(); // aggr немного unfriendly, а немного expression
            if(aggrDef != null) {
                expression = aggrDef.getPropertyExpression();
                if(expression != null)
                    return new ExprsContextModifier(expression);
            }   
        }

        return ContextModifier.EMPTY;
    }
    
    public static ContextInferrer getContextAPInferrer(@Nullable LSFPropertyCalcStatement calcStatement) {
        if(calcStatement != null) {
            LSFPropertyExpression expression = calcStatement.getPropertyExpression();
            if (expression != null)
                return new ExprsContextInferrer(expression);
            LSFAggrPropertyDefinition aggrDef = calcStatement.getExpressionUnfriendlyPD().getAggrPropertyDefinition(); // aggr немного unfriendly, а немного expression
            if(aggrDef != null) {
                expression = aggrDef.getPropertyExpression();
                if(expression != null)
                    return new ExprsContextInferrer(expression);
            }
        }

        return ContextInferrer.EMPTY;
    }

    public static ExplicitContextModifier getExplicitContextModifier(@NotNull LSFPropertyDeclaration decl) {
        LSFPropertyDeclParams declParams = decl.getPropertyDeclParams();
        if (declParams != null)
            return new ExplicitContextModifier(declParams.getClassParamDeclareList());
        return null; 
    }
    // кривовато с unfriendly но пока непонятно как по другому
    public static ContextModifier getContextModifier(@NotNull LSFPropertyStatement sourceStatement) {
        ExplicitContextModifier explicitModifier = getExplicitContextModifier(sourceStatement.getPropertyDeclaration());
        if(explicitModifier != null)
            return explicitModifier;
        return getContextAPModifier(sourceStatement.getPropertyCalcStatement());
    }
    public static ContextModifier getContextModifier(@NotNull LSFActionStatement sourceStatement) {
        ExplicitContextModifier explicitModifier = getExplicitContextModifier(sourceStatement.getPropertyDeclaration());
        if(explicitModifier != null)
            return explicitModifier;
        return ContextModifier.EMPTY;
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFPropertyStatement sourceStatement) {
        return getContextAPInferrer(sourceStatement.getPropertyCalcStatement());
    }
    public static ContextInferrer getContextInferrer(@NotNull LSFActionStatement sourceStatement) {
        LSFListActionPropertyDefinitionBody body = sourceStatement.getListActionPropertyDefinitionBody();
        if(body != null)
            return new ActionInferrer(body);
        return ContextInferrer.EMPTY;
    }

    public static ContextModifier getContextModifier(@NotNull LSFOverridePropertyStatement sourceStatement) {
        return new ExplicitContextModifier(sourceStatement.getMappedPropertyClassParamDeclare());
    }

    public static ContextModifier getContextModifier(@NotNull LSFOverrideActionStatement sourceStatement) {
        return new ExplicitContextModifier(sourceStatement.getMappedActionClassParamDeclare());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFOverridePropertyStatement sourceStatement) {

        List<LSFPropertyExpression> peList = sourceStatement.getPropertyExpressionList();
        return peList.isEmpty() ? ContextInferrer.EMPTY : new ExprsContextInferrer(peList);
    }
    public static ContextInferrer getContextInferrer(@NotNull LSFOverrideActionStatement sourceStatement) {

        LSFPropertyExpression pe = sourceStatement.getPropertyExpression();
        ContextInferrer result = (pe == null ? ContextInferrer.EMPTY : new ExprsContextInferrer(pe));

        LSFListActionPropertyDefinitionBody body = sourceStatement.getListActionPropertyDefinitionBody();
        if (body != null) {
            ActionExprInferrer actionInfer = new ActionExprInferrer(body);

            result = pe == null ? actionInfer : new OverrideContextInferrer(actionInfer, result);
        }
        return result;
    }

    public static ContextModifier getContextModifier(@NotNull LSFConstraintStatement sourceStatement) {
        LSFPropertyExpression decl = sourceStatement.getPropertyExpression();
        if (decl != null) {
            return new ExprsContextModifier(decl);
        } else {
            return ContextModifier.EMPTY;
        }
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFConstraintStatement sourceStatement) {
        LSFPropertyExpression decl = sourceStatement.getPropertyExpression();
        if (decl != null) {
            return new ExprsContextInferrer(decl);
        } else {
            return ContextInferrer.EMPTY;
        }
    }

    public static ContextModifier getContextModifier(@NotNull LSFMessagePropertyExpression sourceStatement) {
        return new ExprsContextModifier(sourceStatement.getPropertyExpression());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFMessagePropertyExpression sourceStatement) {
        LSFPropertyExpression decl = sourceStatement.getPropertyExpression();
        return new ExprsContextInferrer(decl);
    }

    public static ContextModifier getContextModifier(@NotNull LSFFollowsStatement sourceStatement) {
        return new ExplicitContextModifier(sourceStatement.getMappedPropertyClassParamDeclare());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFFollowsStatement sourceStatement) {
        return new MappedPropertyContextInferrer(sourceStatement.getMappedPropertyClassParamDeclare());
    }

    public static ContextModifier getContextModifier(@NotNull LSFEventStatement sourceStatement) {
        LSFPropertyExpression decl = sourceStatement.getPropertyExpression();
        if (decl == null)
            return ContextModifier.EMPTY;
        return new ExprsContextModifier(decl);
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFEventStatement sourceStatement) {
        LSFPropertyExpression decl = sourceStatement.getPropertyExpression();
        if (decl == null)
            return ContextInferrer.EMPTY;
        return new ExprsContextInferrer(decl);
    }

    public static ContextModifier getContextModifier(@NotNull LSFGlobalEventStatement sourceStatement) {
        return ContextModifier.EMPTY;
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFGlobalEventStatement sourceStatement) {
        return ContextInferrer.EMPTY;
    }

    public static ContextModifier getContextModifier(@NotNull LSFNavigatorStatement sourceStatement) {
        return ContextModifier.EMPTY;
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFNavigatorStatement sourceStatement) {
        return ContextInferrer.EMPTY;
    }

    public static ContextModifier getContextModifier(@NotNull LSFWindowCreateStatement sourceStatement) {
        return ContextModifier.EMPTY;
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFWindowCreateStatement sourceStatement) {
        return ContextInferrer.EMPTY;
    }

    public static ContextModifier getContextModifier(@NotNull LSFWriteWhenStatement sourceStatement) {
        return new ExplicitContextModifier(sourceStatement.getMappedPropertyClassParamDeclare());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFWriteWhenStatement sourceStatement) {
        ContextInferrer result = new MappedPropertyContextInferrer(sourceStatement.getMappedPropertyClassParamDeclare());
        List<LSFPropertyExpression> peList = sourceStatement.getPropertyExpressionList();
        if (peList.size() == 2)
            result = new AndContextInferrer(result, new ExprsContextInferrer(peList.get(1)));
        return result;
    }

    public static ContextModifier getContextModifier(@NotNull LSFAspectStatement sourceStatement) {
        LSFMappedActionClassParamDeclare decl = sourceStatement.getMappedActionClassParamDeclare();
        if (decl == null)
            return ContextModifier.EMPTY;
        return new ExplicitContextModifier(decl);
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFAspectStatement sourceStatement) {
        LSFMappedActionClassParamDeclare decl = sourceStatement.getMappedActionClassParamDeclare();
        if (decl == null)
            return ContextInferrer.EMPTY;
        return new MappedActionContextInferrer(decl);
    }

    private static List<LSFPropertyExpression> getContextExprs(@Nullable LSFGroupPropertyBody sourceStatement) {
        if(sourceStatement == null)
            return Collections.emptyList();
        List<LSFPropertyExpression> result = new ArrayList<>();
        result.addAll(sourceStatement.getNonEmptyPropertyExpressionList().getPropertyExpressionList());
        LSFOrderPropertyBy orderBy = sourceStatement.getOrderPropertyBy();
        if (orderBy != null)
            result.addAll(orderBy.getNonEmptyPropertyExpressionList().getPropertyExpressionList());
        LSFPropertyExpression pe = sourceStatement.getPropertyExpression();
        if (pe != null)
            result.add(pe);
        return result;
    }

    public static List<LSFPropertyExpression> getContextExprs(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        List<LSFPropertyExpression> result = new ArrayList<>();
        LSFGroupPropertyBody body = sourceStatement.getGroupPropertyBody();
        if  (body != null)
            result.addAll(getContextExprs(body));
        LSFGroupPropertyBy by = sourceStatement.getGroupPropertyBy();
        if (by != null) {
            LSFNonEmptyPropertyExpressionList neList = by.getNonEmptyPropertyExpressionList();
            if(neList != null)
                result.addAll(neList.getPropertyExpressionList());
        }
        return result;
    }

    public static ContextModifier getContextModifier(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        return new ExprsContextModifier(getContextExprs(sourceStatement));
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        return new ExprsContextInferrer(getContextExprs(sourceStatement));
    }

    public static ContextModifier getContextModifier(@NotNull LSFGroupExprPropertyDefinition sourceStatement) {
        return new ExprsContextModifier(getContextExprs(sourceStatement.getGroupPropertyBody()));
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFGroupExprPropertyDefinition sourceStatement) {
        return new ExprsContextInferrer(getContextExprs(sourceStatement.getGroupPropertyBody()));
    }

    public static ContextModifier getContextModifier(@NotNull LSFPropertyExprObject sourceStatement) {
        return getContextAPModifier(sourceStatement.getPropertyCalcStatement()); // sourceStatement.getActionUnfriendlyPD(), sourceStatement.getListActionPropertyDefinitionBody());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFPropertyExprObject sourceStatement) {
        return getContextAPInferrer(sourceStatement.getPropertyCalcStatement()); //sourceStatement.getActionUnfriendlyPD(), sourceStatement.getListActionPropertyDefinitionBody());
    }

    public static ContextModifier getContextModifier(@NotNull LSFForActionPropertyMainBody sourceStatement) {
        ContextModifier result = null;

        LSFPropertyExpression pe = sourceStatement.getPropertyExpression();
        if (pe != null)
            result = new ExprsContextModifier(pe);
        LSFForAddObjClause addObjClause = sourceStatement.getForAddObjClause();
        if (addObjClause != null) {
            ContextModifier addObjModifier = new AddObjectContextModifier(addObjClause);
            if (result == null)
                result = addObjModifier;
            else
                result = new AndContextModifier(result, addObjModifier);
        }
        return result != null ? result : ContextModifier.EMPTY;
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFForActionPropertyMainBody sourceStatement) {
        return new ActionExprInferrer(((LSFForActionPropertyDefinitionBody) sourceStatement.getParent()));
    }

    public static ContextModifier getContextModifier(@NotNull LSFNewActionPropertyDefinitionBody sourceStatement) {
        return new AddObjectContextModifier(sourceStatement.getForAddObjClause());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFNewActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getContextModifier(@NotNull LSFRecalculateActionPropertyDefinitionBody sourceStatement) {
        return new ExprsContextModifier(sourceStatement.getPropertyExpressionList());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFRecalculateActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getContextModifier(@NotNull LSFWhileActionPropertyDefinitionBody sourceStatement) {
        return new ExprsContextModifier(sourceStatement.getPropertyExpression());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFWhileActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getDoContextModifier(@NotNull LSFDialogActionPropertyDefinitionBody sourceStatement) {
        ContextModifier result = null;

        LSFFormActionObjectList formList = sourceStatement.getFormActionObjectList();
        if(formList != null) {
            for(LSFFormActionObjectUsage object : formList.getFormActionObjectUsageList()) {
                if(object.getObjectInputProps() != null) {
                    ContextModifier inputModifier = new ExprParamContextModifier(object);
                    if (result == null)
                        result = inputModifier;
                    else
                        result = new AndContextModifier(result, inputModifier);
                }
            }
        }
//        LSFFormSingleActionObject singleObject = sourceStatement.getFormSingleActionObject();
//        if(singleObject != null) {
//            if(singleObject.getObjectInputProps() != null) {
//                ContextModifier inputModifier = new InputContextModifier(singleObject);
//                if (result == null)
//                    result = inputModifier;
//                else
//                    result = new AndContextModifier(result, inputModifier);
//            }
//        }
        return result != null ? result : ContextModifier.EMPTY;
    }

    public static ContextInferrer getDoContextInferrer(@NotNull LSFDialogActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getDoContextModifier(@NotNull LSFImportActionPropertyDefinitionBody sourceStatement) {
        ContextModifier result = new ExprParamContextModifier(LSFElementGenerator.getRowParam(sourceStatement.getProject()));

        LSFNonEmptyImportFieldDefinitions fieldDefs = sourceStatement.getNonEmptyImportFieldDefinitions();
        if(fieldDefs != null) {
            for(LSFImportFieldDefinition fieldDef : fieldDefs.getImportFieldDefinitionList()) {
                ContextModifier inputModifier = new ExprParamContextModifier(fieldDef);
                if (result == null)
                    result = inputModifier;
                else
                    result = new AndContextModifier(result, inputModifier);
            }
        }
        return result != null ? result : ContextModifier.EMPTY;
    }

    public static ContextInferrer getDoContextInferrer(@NotNull LSFImportActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getDoContextModifier(@NotNull LSFInputActionPropertyDefinitionBody sourceStatement) {
        return new ExprParamContextModifier(sourceStatement.getParamDeclare());
    }

    public static ContextModifier getContextModifier(@NotNull LSFContextFiltersClause sourceStatement) {
        FormContext formContext = PsiTreeUtil.getParentOfType(sourceStatement, FormContext.class);
        Set<LSFObjectDeclaration> objects = formContext != null ? LSFFormExtendImpl.processFormContext(formContext, LSFFormExtend::getObjectDecls, formContext.getTextOffset(), LSFLocalSearchScope.createFrom(sourceStatement), true, false) : null;

        return (offset, currentParams) -> objects != null ? new ArrayList<>(objects) : new ArrayList<>();
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFContextFiltersClause sourceStatement) {
        return new ExprsContextInferrer(sourceStatement.getPropertyExpressionList());
    }

    public static ContextModifier getContextModifier(@NotNull LSFObjectListInputProps sourceStatement) {
        LSFFormActionObjectUsage formActionObject = PsiTreeUtil.getParentOfType(sourceStatement, LSFFormActionObjectUsage.class);
        return (offset, currentParams) -> Collections.singletonList(formActionObject);
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFObjectListInputProps sourceStatement) {
        return new ExprsContextInferrer(sourceStatement.getPropertyExpression());
    }

    public static ContextModifier getContextModifier(@NotNull LSFContextActions sourceStatement) {
        LSFInputActionPropertyDefinitionBody inputAction = PsiTreeUtil.getParentOfType(sourceStatement, LSFInputActionPropertyDefinitionBody.class);
        if (inputAction != null) {
            LSFParamDeclare paramDeclare = inputAction.getParamDeclare();
            LSFListWhereInputProps listWhereInputProps = inputAction.getListWhereInputProps();
            if (paramDeclare != null && paramDeclare.resolveClass() instanceof CustomClassSet && listWhereInputProps != null) {
                LSFListInputProp listInputProp = listWhereInputProps.getListInputProp();
                if (listInputProp != null) {
                    LSFPropertyExpression listPropertyExpression = listInputProp.getPropertyExpression();
                    if (listPropertyExpression != null) {
                        LSFExClassSet valueClass = listPropertyExpression.resolveValueClass(false);
                        if (valueClass != null) {
                            return (offset, currentParams) -> Collections.singletonList(LSFElementGenerator.createExprParamDeclaration(paramDeclare.getNameIdentifier(), valueClass.classSet));
                        }
                    }
                }
            }

            return (offset, currentParams) -> Collections.singletonList(paramDeclare);
        }
        return ContextModifier.EMPTY;
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFContextActions sourceStatement) {
        return ContextInferrer.EMPTY;
    }

    public static ContextModifier getContextModifier(@NotNull LSFListActionDefinitionBody sourceStatement) {
        return new ActionContextModifier(PsiTreeUtil.getParentOfType(sourceStatement, LSFListActionPropertyDefinitionBody.class));
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFListActionDefinitionBody sourceStatement) {
        return ContextInferrer.EMPTY;
    }

    public static ContextModifier getContextModifier(@NotNull LSFListWhereInputProps sourceStatement) {
        LSFInputActionPropertyDefinitionBody inputAction = PsiTreeUtil.getParentOfType(sourceStatement, LSFInputActionPropertyDefinitionBody.class);
        LSFClassSet resolveClass = inputAction.resolveClass();
        if(resolveClass instanceof DataClass) {
            List<LSFPropertyExpression> propertyExpressionList = new ArrayList<>();
            LSFListInputProp listInputProp = sourceStatement.getListInputProp();
            if(listInputProp != null) {
                LSFPropertyExpression propertyExpression = listInputProp.getPropertyExpression();
                if(propertyExpression != null) {
                    propertyExpressionList.add(propertyExpression);
                }
            }
            LSFWhereInputProp whereInputProp = sourceStatement.getWhereInputProp();
            if(whereInputProp != null) {
                propertyExpressionList.add(whereInputProp.getPropertyExpression());
            }
            return new ExprsContextModifier(propertyExpressionList);
        } else {
            LSFParamDeclare paramDeclare = inputAction.getParamDeclare();
            if(paramDeclare == null)
                return ContextModifier.EMPTY;

            return (offset, currentParams) -> Collections.singletonList(inputAction.getParamDeclare());
        }
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFListWhereInputProps sourceStatement) {
        return ContextInferrer.EMPTY;
    }

    public static ContextModifier getContextModifier(@NotNull LSFDoMainBody sourceStatement) {
        ExtendDoParamContext context = PsiTreeUtil.getParentOfType(sourceStatement, ExtendDoParamContext.class);
        return context == null ? null : context.getDoContextModifier();
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFDoMainBody sourceStatement) {
        ExtendDoParamContext context = PsiTreeUtil.getParentOfType(sourceStatement, ExtendDoParamContext.class);
        return context == null ? null : context.getDoContextInferrer();
    }

    public static ContextInferrer getDoContextInferrer(@NotNull LSFInputActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getDoContextModifier(@NotNull LSFConfirmActionPropertyDefinitionBody sourceStatement) {
        return new ExprParamContextModifier(sourceStatement.getParamDeclare());
    }

    public static ContextInferrer getDoContextInferrer(@NotNull LSFConfirmActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    private static List<LSFExprParameterUsage> getContextExprs(@NotNull LSFAssignActionPropertyDefinitionBody sourceStatement) {
        LSFChangePropertyBody changePropertyBody = sourceStatement.getChangePropertyBody();
        if (changePropertyBody != null) {
            List<LSFExprParameterUsage> result = new ArrayList<>();
            Set<String> names = new HashSet<>();
            for(LSFParameterOrExpression paramOrExpr : getList(changePropertyBody.getParameterOrExpressionList())) {
                LSFExprParameterUsage paramUsage = paramOrExpr.getExprParameterUsage();
                if(paramUsage != null) {
                    LSFId simpleName = paramUsage.getExprParameterNameUsage().getSimpleName();
                    if(simpleName != null) {
                        String name = simpleName.getName();
                        if(names.add(name))
                            result.add(paramUsage);
                    }
                }
            }
            return result;
        }
        return Collections.emptyList();
    }

    public static ContextModifier getContextModifier(@NotNull LSFAssignActionPropertyDefinitionBody sourceStatement) {
        return new ExprParamUsageContextModifier(getContextExprs(sourceStatement));
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFAssignActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    private static List<LSFPropertyExpression> getContextExprs(@NotNull LSFExportDataActionPropertyDefinitionBody sourceStatement) {
        List<LSFPropertyExpression> result = new ArrayList<>();
        LSFNonEmptyAliasedPropertyExpressionList neList = sourceStatement.getNonEmptyAliasedPropertyExpressionList();
        if(neList != null)                
            for(LSFAliasedPropertyExpression ape : neList.getAliasedPropertyExpressionList())
                result.add(ape.getPropertyExpression());
        LSFWherePropertyExpression whereProperty = sourceStatement.getWherePropertyExpression();
        if(whereProperty != null) {
            result.add(whereProperty.getPropertyExpression());
        }
        return result;
    }

    public static ContextModifier getContextModifier(@NotNull LSFExportDataActionPropertyDefinitionBody sourceStatement) {
        return new ExprsContextModifier(getContextExprs(sourceStatement));
    }
    
    public static ContextInferrer getContextInferrer(@NotNull LSFExportDataActionPropertyDefinitionBody sourceStatement) {
        return new ExprsContextInferrer(getContextExprs(sourceStatement));
    }

    private static List<LSFPropertyExpression> getContextExprs(@NotNull LSFJsonPropertyDefinition sourceStatement) {
        List<LSFPropertyExpression> result = new ArrayList<>();
        LSFNonEmptyAliasedPropertyExpressionList neList = sourceStatement.getNonEmptyAliasedPropertyExpressionList();
        if(neList != null)
            for(LSFAliasedPropertyExpression ape : neList.getAliasedPropertyExpressionList())
                result.add(ape.getPropertyExpression());
        LSFWherePropertyExpression whereProperty = sourceStatement.getWherePropertyExpression();
        if(whereProperty != null) {
            result.add(whereProperty.getPropertyExpression());
        }
        return result;
    }

    public static ContextModifier getContextModifier(@NotNull LSFJsonPropertyDefinition sourceStatement) {
        return new ExprsContextModifier(getContextExprs(sourceStatement));
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFJsonPropertyDefinition sourceStatement) {
        return new ExprsContextInferrer(getContextExprs(sourceStatement));
    }

    public static ContextModifier getContextModifier(@NotNull LSFChangeClassActionPropertyDefinitionBody sourceStatement) {
        return getContextModifier(sourceStatement.getParameterOrExpression());
    }
    
    private static ContextModifier getContextModifier(@Nullable LSFParameterOrExpression body) {
        LSFExprParameterUsage exprParameterUsage = null;
        if(body != null)
            exprParameterUsage = body.getExprParameterUsage();
        return new ExprParamUsageContextModifier(exprParameterUsage == null ? Collections.<LSFExprParameterUsage>emptyList() : singletonList(exprParameterUsage));
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFChangeClassActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getContextModifier(@NotNull LSFDeleteActionPropertyDefinitionBody sourceStatement) {
        return getContextModifier(sourceStatement.getParameterOrExpression());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFDeleteActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getContextModifier(@NotNull LSFNewWhereActionPropertyDefinitionBody sourceStatement) {
        LSFPropertyExpression expression = sourceStatement.getPropertyExpression();
        if (expression == null)
            return ContextModifier.EMPTY;
        return new ExprsContextModifier(sourceStatement.getPropertyExpression());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFNewWhereActionPropertyDefinitionBody sourceStatement) {
        return ContextInferrer.EMPTY;
    }

    public static ContextModifier getContextModifier(@NotNull LSFIndexStatement sourceStatement) {
        LSFNonEmptyMappedPropertyOrSimpleExprParamList list = sourceStatement.getNonEmptyMappedPropertyOrSimpleExprParamList();
        if (list == null)
            return ContextModifier.EMPTY;
        return new MappedPropertyOrSimpleExprParamContextModifier(list.getMappedPropertyOrSimpleExprParamList());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFIndexStatement sourceStatement) {
        LSFNonEmptyMappedPropertyOrSimpleExprParamList list = sourceStatement.getNonEmptyMappedPropertyOrSimpleExprParamList();
        if (list != null)
            return new MappedPropertyOrSimpleExprParamContextInferrer(list.getMappedPropertyOrSimpleExprParamList());
        return ContextInferrer.EMPTY;
    }

    // CLASSES

    public static LSFExClassSet op(@Nullable LSFExClassSet class1, @Nullable LSFExClassSet class2, boolean or) {
        return op(class1, class2, or, false);
    }

    public static LSFExClassSet op(@Nullable LSFExClassSet class1, @Nullable LSFExClassSet class2, boolean or, boolean string) {
        assert or || !string;

        if(class1 == null && class2 == null)
            return null;
        
        if(class1 == null)
            return class2.opNull(or);
        
        if(class2 == null)
            return class1.opNull(or);
        
        return class1.op(class2, or, string);
    }

    @Nullable
    private static LSFExClassSet or(@Nullable LSFExClassSet class1, @Nullable LSFExClassSet class2, boolean string) {
        return op(class1, class2, true, string);
    }

    @Nullable
    private static LSFExClassSet scale(@Nullable LSFExClassSet class1, @Nullable LSFExClassSet class2, boolean mult) {
        if(class1 == null)
            return class2;

        if(class2 == null)
            return class1;

        if(!(class1.classSet instanceof IntegralClass && class2.classSet instanceof IntegralClass))
            return null;

        return LSFExClassSet.checkNull(((IntegralClass)class1.classSet).scale((IntegralClass)class2.classSet, mult), class1.orAny || class2.orAny);
    }

    public static boolean containsAll(@NotNull List<LSFClassSet> who, @NotNull List<LSFClassSet> what, boolean falseImplicitClass) {
        for (int i = 0, size = who.size(); i < size; i++) {
            LSFClassSet whoClass = who.get(i);
            LSFClassSet whatClass = what.get(i);
            if (whoClass == null && whatClass == null)
                continue;

            if (whoClass != null && whatClass != null && !whoClass.containsAll(whatClass, true))
                return false;

            if (whatClass != null)
                continue;

            if (falseImplicitClass)
                return false;
        }
        return true;
    }

    public static boolean haveCommonChilds(@NotNull List<LSFClassSet> classes1, @NotNull List<LSFClassSet> classes2, GlobalSearchScope scope) {

        for (int i = 0, size = classes1.size(); i < size; i++) {
            LSFClassSet class1 = classes1.get(i);
            LSFClassSet class2 = classes2.get(i);
            if (class1 != null && class2 != null && !class1.haveCommonChildren(class2, scope)) // потом переделать haveCommonChildren
                return false;
        }
        return true;
    }

    public static int getCommonChildrenCount(@NotNull List<LSFClassSet> classes1, @NotNull List<LSFClassSet> classes2, GlobalSearchScope scope) {
        int count = 0;
        for (int i = 0, size = Math.min(classes1.size(), classes2.size()); i < size; i++) {
            LSFClassSet class1 = classes1.get(i);
            LSFClassSet class2 = classes2.get(i);
            if (class1 == null || class2 == null || class1.haveCommonChildren(class2, scope)) { // потом переделать haveCommonChildren
                count++;
            }
        }
        return count;
    }

    public static DataClass resolve(LSFBuiltInClassName builtIn) {
        String name = builtIn.getText();
        return resolveDataClass(name);
    }

    public static DataClass resolveDataClass(String name) {
        if (name.equals("DOUBLE"))
            return DoubleClass.instance;
        if (name.equals("INTEGER"))
            return IntegerClass.instance;
        if (name.equals("LONG"))
            return LongClass.instance;
        if (name.equals("YEAR"))
            return YearClass.instance;

        if (name.startsWith("BPSTRING[")) {
            name = name.substring("BPSTRING[".length(), name.length() - 1);
            return new StringClass(true, false, new ExtInt(Integer.parseInt(name)));
        } else if (name.startsWith("BPISTRING[")) {
            name = name.substring("BPISTRING[".length(), name.length() - 1);
            return new StringClass(true, true, new ExtInt(Integer.parseInt(name)));
        } else if (name.startsWith("STRING[")) {
            name = name.substring("STRING[".length(), name.length() - 1);
            return new StringClass(false, false, new ExtInt(Integer.parseInt(name)));
        } else if (name.startsWith("ISTRING[")) {
            name = name.substring("ISTRING[".length(), name.length() - 1);
            return new StringClass(false, true, new ExtInt(Integer.parseInt(name)));
        } else if (name.startsWith("NUMERIC[")) {
            String length = name.substring("NUMERIC[".length(), name.indexOf(","));
            String precision = name.substring(name.indexOf(",") + 1, name.length() - 1);
            return new NumericClass(Integer.parseInt(length), Integer.parseInt(precision));
        } else if (name.equals("NUMERIC")) {
            return new NumericClass(ExtInt.UNLIMITED, ExtInt.UNLIMITED);
        } else if (name.equals("TEXT")) {
            return new TextClass();
        } else if (name.equals("RICHTEXT")) {
            return new RichTextClass();
        } else if (name.equals("HTMLTEXT")) {
            return new HTMLTextClass();
        } else if (name.equals("HTML")) {
            return new HTMLStringClass();
        } else if (name.equals("BPSTRING")) {
            return new StringClass(true, false, ExtInt.UNLIMITED);
        } else if (name.equals("BPISTRING")) {
            return new StringClass(true, true, ExtInt.UNLIMITED);
        } else if (name.equals("STRING")) {
            return new StringClass(false, false, ExtInt.UNLIMITED);
        } else if (name.equals("ISTRING")) {
            return new StringClass(false, true, ExtInt.UNLIMITED);
        } else if (name.startsWith("INTERVAL[")) {
            String type = name.substring("INTERVAL[".length(), name.indexOf("]"));
            switch (type) {
                case "DATE" :
                    return DateIntervalClass.instance;
                case "TIME" :
                    return TimeIntervalClass.instance;
                case "DATETIME" :
                    return DateTimeIntervalClass.instance;
                case "ZDATETIME" :
                    return ZDateTimeIntervalClass.instance;
            }
        } else if (name.startsWith("DATETIME[")) {
            return DateTimeClass.instance;
        } else if (name.startsWith("ZDATETIME[")) {
            return ZDateTimeClass.instance;
        } else if (name.startsWith("TIME[")) {
            return TimeClass.instance;
        }

        switch (name) {
            case "WORDFILE":
                return WordClass.instance;
            case "IMAGEFILE":
                return ImageClass.instance;
            case "PDFFILE":
                return PDFClass.instance;
            case "VIDEOFILE":
                return VideoClass.instance;
            case "DBFFILE":
                return DBFClass.instance;
            case "RAWFILE":
                return RawClass.instance;
            case "FILE":
                return DynamicFormatFileClass.instance;
            case "EXCELFILE":
                return ExcelClass.instance;
            case "TEXTFILE":
                return TXTClass.instance;
            case "CSVFILE":
                return CSVClass.instance;
            case "HTMLFILE":
                return HTMLClass.instance;
            case "JSONFILE":
                return JSONFileClass.instance;
            case "XMLFILE":
                return XMLClass.instance;
            case "TABLEFILE":
                return TableClass.instance;
            case "NAMEDFILE":
                return NamedFileClass.instance;
            case "WORDLINK":
                return WordLinkClass.instance;
            case "IMAGELINK":
                return ImageLinkClass.instance;
            case "PDFLINK":
                return PDFLinkClass.instance;
            case "VIDEOLINK":
                return VideoLinkClass.instance;
            case "DBFLINK":
                return DBFLinkClass.instance;
            case "RAWLINK":
                return RawLinkClass.instance;
            case "LINK":
                return DynamicFormatLinkClass.instance;
            case "EXCELLINK":
                return ExcelLinkClass.instance;
            case "TEXTLINK":
                return TXTLinkClass.instance;
            case "CSVLINK":
                return CSVLinkClass.instance;
            case "HTMLLINK":
                return HTMLLinkClass.instance;
            case "JSONLINK":
                return JSONLinkClass.instance;
            case "XMLLINK":
                return XMLLinkClass.instance;
            case "TABLELINK":
                return TableLinkClass.instance;
            case "BOOLEAN":
                return LogicalClass.instance;
            case "TBOOLEAN":
                return LogicalClass.threeStateInstance;
            case "DATE":
                return DateClass.instance;
            case "TIME":
                return TimeClass.instance;
            case "DATETIME":
                return DateTimeClass.instance;
            case "ZDATETIME":
                return ZDateTimeClass.instance;
            case "COLOR":
                return ColorClass.instance;
            case "JSON":
                return JSONClass.instance;
            case "TSVECTOR":
                return TSVectorClass.instance;
            case "TSQUERY":
                return TSQueryClass.instance;
        }

        return new SimpleDataClass(name);
    }

    @Nullable
    public static LSFClassDeclaration resolveClassDecl(@Nullable LSFCustomClassUsage usage) {
        if (usage == null) {
            return null;
        }
        return usage.resolveDecl();
    }

    @Nullable
    public static LSFClassSet resolveClass(@Nullable LSFClassName className) {
        if (className == null) {
            return null;
        }
        LSFBuiltInClassName builtInClassName = className.getBuiltInClassName();
        if (builtInClassName != null)
            return resolve(builtInClassName);
        return resolveClass(className.getCustomClassUsage());
    }

    @Nullable
    public static CustomClassSet resolveClass(@Nullable LSFCustomClassUsage usage) {
        LSFClassDeclaration classDecl = resolveClassDecl(usage);
        return classDecl == null ? null : new CustomClassSet(classDecl);
    }

    @NotNull
    public static List<LSFClassSet> resolveClasses(@Nullable LSFClassNameList classNameList) {
        if (classNameList == null) {
            return Collections.emptyList();
        }

        LSFNonEmptyClassNameList ne = classNameList.getNonEmptyClassNameList();
        if (ne == null) {
            return Collections.emptyList();
        }

        List<LSFClassSet> result = new ArrayList<>();
        for (LSFClassName className : ne.getClassNameList())
            result.add(resolveClass(className));
        return result;
    }

    @Nullable
    public static LSFClassSet resolveClass(@NotNull LSFClassParamDeclare sourceStatement) {
        LSFClassSet explicitClass = resolveExplicitClass(sourceStatement);
        if(explicitClass != null)
            return explicitClass;

        LSFPropertyDeclaration propDecl = PsiTreeUtil.getParentOfType(sourceStatement, LSFPropertyDeclaration.class);
        if(propDecl != null) {
            LSFActionOrGlobalPropDeclarationImpl statement = PsiTreeUtil.getParentOfType(sourceStatement, LSFActionOrGlobalPropDeclarationImpl.class);
            if(statement.isUnfriendly()) { // оптимизация, хотя в некоторых случаях все же меняет поведение
                int paramNum = PsiTreeUtil.getParentOfType(sourceStatement, LSFNonEmptyClassParamDeclareList.class).getClassParamDeclareList().indexOf(sourceStatement);
                assert paramNum >= 0;
                List<LSFExClassSet> paramClasses = statement.resolveExParamClasses();// для кэширования, так можно было бы resolveValueParamClasses вызвать
                if (paramClasses != null && paramNum < paramClasses.size())
                    return LSFExClassSet.fromEx(paramClasses.get(paramNum));
            }
        }
        return resolveExplicitClass(sourceStatement);
    }

    public static LSFClassName getClassName(@NotNull LSFClassParamDeclare sourceStatement) {
        LSFAggrParamPropDeclare aggDeclare = sourceStatement.getAggrParamPropDeclare();
        if(aggDeclare != null)
            return aggDeclare.getClassName();
        return null;
    }

    @NotNull
    public static LSFParamDeclare getParamDeclare(@NotNull LSFClassParamDeclare sourceStatement) {
        LSFAggrParamPropDeclare aggDeclare = sourceStatement.getAggrParamPropDeclare();
        if(aggDeclare != null)
            return aggDeclare.getParamDeclare();
        return sourceStatement.getUntypedParamDeclare().getParamDeclare();
    }
    
    @Nullable
    public static LSFClassSet resolveExplicitClass(@NotNull LSFClassParamDeclare sourceStatement) {
        LSFClassName className = sourceStatement.getClassName();
        if (className == null)
            return null;
        return resolveClass(className);
    }

    public static List<LSFClassSet> resolveExplicitClass(LSFClassParamDeclareList classNameList) {
        List<LSFClassSet> result = new ArrayList<>();

        LSFNonEmptyClassParamDeclareList ne = classNameList.getNonEmptyClassParamDeclareList();
        if (ne == null)
            return result;
        for (LSFClassParamDeclare classParamDeclare : ne.getClassParamDeclareList())
            result.add(resolveExplicitClass(classParamDeclare));
        return result;
    }

    @Nullable
    public static LSFClassSet resolveClass(@NotNull LSFForAddObjClause sourceStatement) {
        return resolveClass(sourceStatement.getCustomClassUsage());
    }

    @Nullable
    public static LSFClassSet resolveClass(@NotNull LSFFormActionObjectUsage sourceStatement) {
        return sourceStatement.getObjectUsage().resolveClass();
    }

    @Nullable
    public static LSFClassSet resolveClass(@NotNull LSFImportFieldDefinition sourceStatement) {
        return resolve(sourceStatement.getBuiltInClassName());
    }

    @Nullable
    public static LSFClassSet resolveClass(@NotNull LSFInputActionPropertyDefinitionBody sourceStatement) {
        LSFBuiltInClassName builtInClassName = sourceStatement.getBuiltInClassName();
        if(builtInClassName != null)
            return resolve(builtInClassName);

        LSFClassOrExpression list = sourceStatement.getClassOrExpression();
        if(list != null) {
            LSFCustomClassUsage customClassUsage = list.getCustomClassUsage();
            if(customClassUsage != null)
                return resolveClass(customClassUsage);
            else
                return LSFExClassSet.fromEx(list.getPropertyExpression().resolveValueClass(false));
        }
        
        return null;
    }    
    
    @Nullable
    public static LSFClassSet resolveClass(@NotNull LSFConfirmActionPropertyDefinitionBody sourceStatement) {
        return resolveDataClass("BOOLEAN");
    }    
    
    // PROPERTYEXPRESSION.RESOLVEVALUECLASS

    @Nullable
    public static LSFExClassSet resolveExpressionInferredValueClass(@Nullable LSFExpression sourceStatement, @Nullable InferExResult inferred) {
        return sourceStatement == null ? null : sourceStatement.resolveInferredValueClass(inferred);
    }

    @Nullable
    public static LSFExClassSet resolveValueClass(@NotNull LSFPropertyExpression sourceStatement, boolean infer) {
        return sourceStatement.resolveInferredValueClass(infer ? sourceStatement.inferParamClasses(null).finishEx() : null);
    }

    @Nullable
    public static LSFValueClass resolveValueClass(@Nullable LSFClassName className) {
        if (className == null) {
            return null;
        }
        LSFBuiltInClassName builtInClassName = className.getBuiltInClassName();
        if (builtInClassName != null)
            return resolve(builtInClassName);

        return resolveClassDecl(className.getCustomClassUsage());
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFPropertyExpression sourceStatement, @Nullable InferExResult inferred) {
        return resolveInferredValueClass(sourceStatement.getIfPE(), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFIfPE sourceStatement, @Nullable InferExResult inferred) {
        return resolveInferredValueClass(sourceStatement.getOrPEList().get(0), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFOrPE sourceStatement, @Nullable InferExResult inferred) {
        List<LSFXorPE> list = sourceStatement.getXorPEList();
        if (list.size() > 1)
            return LSFExClassSet.logical;
        return resolveInferredValueClass(list.get(0), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFXorPE sourceStatement, @Nullable InferExResult inferred) {
        List<LSFAndPE> list = sourceStatement.getAndPEList();
        if (list.size() > 1)
            return LSFExClassSet.logical;
        return resolveInferredValueClass(list.get(0), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFAndPE sourceStatement, @Nullable InferExResult inferred) {
        List<LSFNotPE> list = sourceStatement.getNotPEList();
        if (list.size() > 1)
            return LSFExClassSet.logical;
        return resolveInferredValueClass(list.get(0), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFNotPE sourceStatement, @Nullable InferExResult inferred) {
        LSFEqualityPE eqPE = sourceStatement.getEqualityPE();
        if (eqPE != null)
            return resolveInferredValueClass(eqPE, inferred);
        return LSFExClassSet.logical;
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFEqualityPE sourceStatement, @Nullable InferExResult inferred) {
        List<LSFRelationalPE> list = sourceStatement.getRelationalPEList();
        if (list.size() == 2)
            return LSFExClassSet.logical;
        return resolveInferredValueClass(list.get(0), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFRelationalPE sourceStatement, @Nullable InferExResult inferred) {
        List<LSFLikePE> list = sourceStatement.getLikePEList();
        if (list.size() == 2)
            return LSFExClassSet.logical;

        return resolveInferredValueClass(list.get(0), inferred);
    }

    private static LSFExClassSet orClasses(List<LSFExClassSet> classes, boolean string) {
        LSFExClassSet result = null;
        for (int i = 0; i < classes.size(); i++) {
            LSFExClassSet classSet = classes.get(i);
            if (i == 0)
                result = classSet;
            else
                result = or(result, classSet, string);
        }
        return result;
    }

    private static LSFExClassSet scaleClasses(List<LSFExClassSet> classes, List<Boolean> mults) {
        LSFExClassSet result = null;
        for (int i = 0; i < classes.size(); i++) {
            LSFExClassSet classSet = classes.get(i);
            if (i == 0)
                result = classSet;
            else
                result = scale(result, classSet, mults.get(i-1));
        }
        return result;
    }

    private static LSFExClassSet orStringClasses(List<LSFExClassSet> classes) {
        boolean hasString = false;
        List<LSFExClassSet> fixedClasses = new ArrayList<>();
        for (LSFExClassSet ex : classes) {
            if (ex != null) {
                LSFClassSet classSet = ex.classSet;
                if (classSet instanceof StringClass)
                    hasString = true;
            }
            fixedClasses.add(ex);
        }
        return orClasses(fixedClasses, hasString);
    }

    private static LSFExClassSet concatStringClasses(List<LSFExClassSet> classes, LSFExClassSet separatorClass) {
        int separatorLength = separatorClass != null ? separatorClass.classSet.getCharLength().value : 0;

        ExtInt length = ExtInt.ZERO;
        boolean caseInsensitive = false;
        boolean blankPadded = true;
        boolean isText = false;
        for (int i = 0; i < classes.size(); i++) {
            LSFExClassSet cls = classes.get(i);
            if(cls != null) {
                LSFClassSet type = cls.classSet;
                length = length.sum(type.getCharLength());
                if (type instanceof StringClass) {
                    StringClass stringType = (StringClass) type;
                    caseInsensitive = caseInsensitive || stringType.caseInsensitive;
                    blankPadded = blankPadded && stringType.blankPadded;
                    if (stringType instanceof TextClass) {
                        isText = true;
                    }
                }
            }

            if (i > 0) {
                length = length.sum(new ExtInt(separatorLength));
            }
        }

        if(isText)
            return LSFExClassSet.toEx(new TextClass());
        return LSFExClassSet.toEx(new StringClass(blankPadded, caseInsensitive, length));
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFLikePE sourceStatement, @Nullable InferExResult inferred) {
        List<LSFExClassSet> orClasses = new ArrayList<>();
        for (LSFAdditiveORPE pe : sourceStatement.getAdditiveORPEList())
            orClasses.add(resolveInferredValueClass(pe, inferred));
        return orClasses(orClasses, false);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFAdditiveORPE sourceStatement, @Nullable InferExResult inferred) {
        List<LSFExClassSet> orClasses = new ArrayList<>();
        for (LSFAdditivePE pe : sourceStatement.getAdditivePEList())
            orClasses.add(resolveInferredValueClass(pe, inferred));
        return orClasses(orClasses, false);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFAdditivePE sourceStatement, @Nullable InferExResult inferred) {
        List<LSFExClassSet> orClasses = new ArrayList<>();
        for (LSFMultiplicativePE pe : sourceStatement.getMultiplicativePEList())
            orClasses.add(resolveInferredValueClass(pe, inferred));
        return orStringClasses(orClasses);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFMultiplicativePE sourceStatement, @Nullable InferExResult inferred) {
        List<LSFExClassSet> orClasses = new ArrayList<>();
        for (LSFUnaryMinusPE pe : sourceStatement.getUnaryMinusPEList())
            orClasses.add(resolveInferredValueClass(pe, inferred));
        List<Boolean> mults = new ArrayList<>();
        for (LSFTypeMult type : sourceStatement.getTypeMultList())
            mults.add(type.getText().equals("*"));
        return scaleClasses(orClasses, mults);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFUnaryMinusPE sourceStatement, @Nullable InferExResult inferred) {
        LSFUnaryMinusPE unaryMinusPE = sourceStatement.getUnaryMinusPE();
        if (unaryMinusPE != null)
            return resolveInferredValueClass(unaryMinusPE, inferred);

        return resolveExpressionInferredValueClass(sourceStatement.getPostfixUnaryPE(), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFPostfixUnaryPE sourceStatement, @Nullable InferExResult inferred) {
        LSFExClassSet valueClass = resolveInferredValueClass(sourceStatement.getSimplePE(), inferred);

        LSFUintLiteral uintLiteral = sourceStatement.getUintLiteral();
        if (uintLiteral != null) {
            LSFClassSet classSet = LSFExClassSet.fromEx(valueClass);
            if (classSet instanceof ConcatenateClassSet)
                return LSFExClassSet.checkNull(((ConcatenateClassSet) classSet).getSet(Integer.parseInt(uintLiteral.getText()) - 1), valueClass.orAny);
            return null;
        }
        
        LSFTypePropertyDefinition typeDef = sourceStatement.getTypePropertyDefinition();
        if (typeDef != null) {
            if (typeDef.getTypeIs().getText().equals("IS"))
                return LSFExClassSet.logical;
            else {
                LSFExClassSet resolveClass = LSFExClassSet.toEx(resolveClass(typeDef.getClassName()));
                if (resolveClass != null) {
                    if (valueClass == null)
                        valueClass = resolveClass;
                    else
                        valueClass = valueClass.op(resolveClass, false);
                }
            }
        }
        return valueClass;
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFSimplePE sourceStatement, @Nullable InferExResult inferred) {
        LSFPropertyExpression pe = sourceStatement.getPropertyExpression();
        if (pe != null)
            return pe.resolveInferredValueClass(inferred);

        return resolveExpressionInferredValueClass(sourceStatement.getExpressionPrimitive(), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFExpressionPrimitive sourceStatement, @Nullable InferExResult inferred) {
        LSFExprParameterUsage ep = sourceStatement.getExprParameterUsage();
        if (ep != null)
            return resolveInferredValueClass(ep, inferred);

        return resolveExpressionInferredValueClass(sourceStatement.getExpressionFriendlyPD(), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFExprParameterUsage sourceStatement, @Nullable InferExResult inferred) {
        LSFExprParameterNameUsage nameParam = sourceStatement.getExprParameterNameUsage();
        if (nameParam != null)
            return nameParam.resolveInferredClass(inferred);
        return null;
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFExpressionFriendlyPD sourceStatement, @Nullable InferExResult inferred) {
        return ((LSFExpression) sourceStatement.getChildren()[0]).resolveInferredValueClass(inferred);
    }

    private static LSFExClassSet resolveValueClass(LSFPropertyUsage usage, boolean infer) {
        LSFPropDeclaration decl = usage.resolveDecl();
        if (decl != null)
            return decl.resolveExValueClass(infer);
        return null;
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFJoinPropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        LSFPropertyUsage usage = sourceStatement.getPropertyUsage();
        if (usage != null) {
            LSFExClassSet valueClass = resolveValueClass(usage, inferred != null);
            if (valueClass != null)
                return valueClass;
            return null;
        }

        LSFPropertyExprObject exprObject = sourceStatement.getPropertyExprObject();

        assert exprObject != null;

        LSFPropertyCalcStatement pCalcStatement = exprObject.getPropertyCalcStatement();
        if(pCalcStatement == null)
            return null;

        LSFPropertyExpression pe = pCalcStatement.getPropertyExpression();
        if (pe != null)
            return pe.resolveValueClass(inferred != null);

        LSFExpressionUnfriendlyPD expressionUnfriendlyPD = pCalcStatement.getExpressionUnfriendlyPD();

        assert expressionUnfriendlyPD != null;

        return expressionUnfriendlyPD.resolveUnfriendValueClass(inferred != null);
    }

    @Nullable
    private static LSFExClassSet resolveInferredValueClass(List<LSFPropertyExpression> list, @Nullable InferExResult inferred) {
        return resolveInferredValueClass(list, inferred, false);
    }

    @Nullable
    private static LSFExClassSet resolveInferredValueClass(List<LSFPropertyExpression> list, @Nullable InferExResult inferred, boolean string) {
        if (list.size() == 0)
            return null;

        LSFExClassSet result = null;
        for (int i = 0; i < list.size(); i++) {
            LSFExClassSet valueClass = list.get(i).resolveInferredValueClass(inferred);
            if (i == 0)
                result = valueClass;
            else
                result = or(result, valueClass, string);
        }
        return result;
    }

    @Nullable
    private static LSFExClassSet resolveInferredValueClass(@Nullable LSFNonEmptyPropertyExpressionList list, @Nullable InferExResult inferred, boolean string) {
        if (list == null) {
            return null;
        }
        return resolveInferredValueClass(list.getPropertyExpressionList(), inferred, string);
    }

    @Nullable
    private static LSFExClassSet resolveAliasedInferredValueClass(List<LSFAliasedPropertyExpression> list, @Nullable InferExResult inferred, boolean string) {
        if (list.size() == 0)
            return null;

        LSFExClassSet result = null;
        for (int i = 0; i < list.size(); i++) {
            LSFExClassSet valueClass = list.get(i).getPropertyExpression().resolveInferredValueClass(inferred);
            if (i == 0)
                result = valueClass;
            else
                result = or(result, valueClass, string);
        }
        return result;
    }

    @Nullable
    private static LSFExClassSet resolveInferredValueClass(@Nullable LSFNonEmptyAliasedPropertyExpressionList list, @Nullable InferExResult inferred, boolean string) {
        if (list == null) {
            return null;
        }
        return resolveAliasedInferredValueClass(list.getAliasedPropertyExpressionList(), inferred, string);
    }

    @Nullable
    private static LSFExClassSet resolveInferredValueClass(@Nullable LSFNonEmptyPropertyExpressionList list, @Nullable InferExResult inferred) {
        return resolveInferredValueClass(list, inferred, false);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFMultiPropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        return resolveInferredValueClass(sourceStatement.getNonEmptyPropertyExpressionList(), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFOverridePropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        return resolveInferredValueClass(sourceStatement.getNonEmptyPropertyExpressionList(), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFIfElsePropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        List<LSFPropertyExpression> list = sourceStatement.getPropertyExpressionList();
        if (list.isEmpty()) {
            return null;
        }
        return resolveInferredValueClass(list.subList(1, list.size()), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFMaxPropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        return resolveInferredValueClass(sourceStatement.getNonEmptyPropertyExpressionList(), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFCasePropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        List<LSFPropertyExpression> list = new ArrayList<>();
        for (LSFCaseBranchBody caseBranch : sourceStatement.getCaseBranchBodyList()) {
            List<LSFPropertyExpression> propertyExpressionList = caseBranch.getPropertyExpressionList();
            if (propertyExpressionList.size() > 1) {
                list.add(propertyExpressionList.get(1));
            }
        }
        LSFPropertyExpression elseExpr = sourceStatement.getPropertyExpression();
        if (elseExpr != null) {
            list.add(elseExpr);
        }
        return resolveInferredValueClass(list, inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFPartitionPropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        return resolveExpressionInferredValueClass(sourceStatement.getPropertyExpression(), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFRecursivePropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        return resolveInferredValueClass(sourceStatement.getPropertyExpressionList(), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFStructCreationPropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        LSFNonEmptyPropertyExpressionList nonEmptyPropertyExpressionList = sourceStatement.getNonEmptyPropertyExpressionList();
        if (nonEmptyPropertyExpressionList == null) {
            return null;
        }

        List<LSFPropertyExpression> peList = nonEmptyPropertyExpressionList.getPropertyExpressionList();

        LSFClassSet[] sets = new LSFClassSet[peList.size()];
        for (int i = 0; i < sets.length; i++)
            sets[i] = LSFExClassSet.fromEx(resolveInferredValueClass(peList.get(i), inferred));
        return LSFExClassSet.toEx(new ConcatenateClassSet(sets));
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFCastPropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        return LSFExClassSet.toEx(resolve(sourceStatement.getBuiltInClassName()));
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFConcatPropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        LSFNonEmptyPropertyExpressionList nonEmptyPropertyExpressionList = sourceStatement.getNonEmptyPropertyExpressionList();
        LSFPropertyExpression separatorExpression = sourceStatement.getPropertyExpression();
        if (nonEmptyPropertyExpressionList != null && separatorExpression != null) {
            List<LSFExClassSet> orClasses = new ArrayList<>();
            for (LSFPropertyExpression pe : nonEmptyPropertyExpressionList.getPropertyExpressionList())
                orClasses.add(resolveInferredValueClass(pe, inferred));
            return concatStringClasses(orClasses, resolveInferredValueClass(separatorExpression, inferred));
        } else {
            return resolveInferredValueClass(sourceStatement.getNonEmptyPropertyExpressionList(), inferred, true);
        }
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFJsonFormPropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        return LSFExClassSet.logical;
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFJsonPropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        return LSFExClassSet.json;
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFSessionPropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        LSFSessionPropertyType type = sourceStatement.getSessionPropertyType();
        if (type.getText().equals("PREV")) {
            LSFPropertyExpression sourcePropertyExpression = sourceStatement.getPropertyExpression();
            if(sourcePropertyExpression != null) {
                return resolveInferredValueClass(sourcePropertyExpression, inferred);
            }
        }
        return LSFExClassSet.logical;
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFSignaturePropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        return LSFExClassSet.logical;
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFActiveTabPropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        return LSFExClassSet.logical;
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFRoundPropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        LSFExClassSet exClassSet = sourceStatement.getPropertyExpressionList().get(0).resolveInferredValueClass(inferred);
        if (exClassSet != null) {
            if (exClassSet.classSet instanceof NumericClass)
                return new LSFExClassSet(NumericClass.unlimited);
        }
        return resolveInferredValueClass(sourceStatement.getPropertyExpressionList(), inferred, false);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFExpressionLiteral sourceStatement, @Nullable InferExResult inferred) {
        DataClass builtInClass = resolveBuiltInValueClass(sourceStatement);
        if (builtInClass != null) {
            return LSFExClassSet.toEx(builtInClass);
        }
        LSFStaticObjectID staticObject = sourceStatement.getStaticObjectID();
        if (staticObject != null)
            return LSFExClassSet.toEx(resolveClass(staticObject.getCustomClassUsage()));
        return null;
    }

    @Nullable
    public static DataClass resolveBuiltInValueClass(@NotNull LSFExpressionLiteral sourceStatement) {
        if (sourceStatement.getBooleanLiteral() != null)
            return LogicalClass.instance;
        if (sourceStatement.getTbooleanLiteral() != null)
            return LogicalClass.threeStateInstance;
        if (sourceStatement.getUlongLiteral() != null)
            return LongClass.instance;
        if (sourceStatement.getUintLiteral() != null)
            return IntegerClass.instance;
        if (sourceStatement.getUdoubleLiteral() != null)
            return DoubleClass.instance;
        if (sourceStatement.getUnumericLiteral() != null) {
            try {
                String name = sourceStatement.getText();
                String whole = name.substring(0, name.indexOf("."));
                String precision = name.substring(name.indexOf(".") + 1, name.length());
                return new NumericClass(whole.length() + precision.length(), precision.length());
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }
        LSFLocalizedStringValueLiteral stringLiteral = sourceStatement.getExpressionStringLiteral();
        if (stringLiteral != null) {
            if (stringLiteral.isVariable()) {
                return new StringClass(false, false, ExtInt.UNLIMITED);
            } else {
                return new StringClass(false, false, new ExtInt(stringLiteral.getValue().length()));
            }
        }
        if (sourceStatement.getDateTimeLiteral() != null)
            return DateTimeClass.instance;
        if (sourceStatement.getDateLiteral() != null)
            return DateClass.instance;
        if (sourceStatement.getTimeLiteral() != null)
            return TimeClass.instance;
        if (sourceStatement.getColorLiteral() != null)
            return ColorClass.instance;
        return null;
    }

    // UnfriendlyPE.resolveValueClass

    @Nullable
    public static LSFExClassSet resolveUnfriendValueClass(@NotNull LSFExpressionUnfriendlyPD sourceStatement, boolean infer) {
        return ((UnfriendlyPE) sourceStatement.getChildren()[0]).resolveUnfriendValueClass(infer);
    }

    @Nullable
    public static LSFExClassSet resolveUnfriendValueClass(@NotNull LSFDataPropertyDefinition sourceStatement, boolean infer) {
        return LSFExClassSet.toEx(resolveClass(sourceStatement.getClassName()));
    }

    @Nullable
    public static LSFExClassSet resolveUnfriendValueClass(@NotNull LSFNativePropertyDefinition sourceStatement, boolean infer) {
        return LSFExClassSet.toEx(resolveClass(sourceStatement.getClassName()));
    }

    @Nullable
    public static LSFExClassSet resolveUnfriendValueClass(@NotNull LSFAbstractPropertyDefinition sourceStatement, boolean infer) {
        return LSFExClassSet.toEx(resolveClass(sourceStatement.getClassName()));
    }

    @Nullable
    public static LSFExClassSet resolveUnfriendValueClass(@NotNull LSFAbstractActionPropertyDefinition sourceStatement, boolean infer) {
        return null;
    }

    @Nullable
    public static LSFExClassSet resolveUnfriendValueClass(@NotNull LSFCustomActionPropertyDefinitionBody sourceStatement, boolean infer) {
        return null;
    }

    @Nullable
    public static LSFExClassSet resolveUnfriendValueClass(@NotNull LSFExternalActionPropertyDefinitionBody sourceStatement, boolean infer) {
        return null;
    }

    private static InferExResult inferGroupParamClasses(LSFGroupPropertyDefinition sourceStatement) {
        Inferred result = Inferred.EMPTY;
        for (LSFPropertyExpression expr : getContextExprs(sourceStatement))
            result = result.and(inferParamClasses(expr, null));
        return result.finishEx();
    }

    @Nullable
    public static LSFExClassSet resolveUnfriendValueClass(@NotNull LSFGroupPropertyDefinition sourceStatement, boolean infer) {
        return resolveInferredValueClass(sourceStatement.getGroupPropertyBody(), infer ? inferGroupParamClasses(sourceStatement) : null);
    }

    public static LSFExClassSet resolveInferredValueClass(LSFGroupPropertyBody body, InferExResult inferred) {
        LSFExClassSet lsfExClassSet = null;
        if(body != null) {
            lsfExClassSet = resolveInferredValueClass(body.getNonEmptyPropertyExpressionList(), inferred);
            LSFGroupingTypeOrder groupingType = body.getGroupingTypeOrder();
            if (lsfExClassSet != null && groupingType != null && groupingType.getText().equals("CONCAT"))
                lsfExClassSet = LSFExClassSet.text;
        }
        return lsfExClassSet;
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(LSFGroupExprPropertyDefinition groupDef, InferExResult inferred) {
        return resolveInferredValueClass(groupDef.getGroupPropertyBody(), inferred);
    }

    @Nullable
    public static LSFExClassSet resolveUnfriendValueClass(@NotNull LSFFormulaPropertyDefinition sourceStatement, boolean infer) {
        @Nullable LSFTypedIdOrStringLiteralWithCheck typedIdOrStringLiteral = sourceStatement.getTypedIdOrStringLiteralWithCheck();
        if (typedIdOrStringLiteral != null)
            return LSFExClassSet.toEx(resolve(typedIdOrStringLiteral.getBuiltInClassName()));
        return null;
    }

    @Nullable
    public static LSFExClassSet resolveUnfriendValueClass(@NotNull LSFFilterPropertyDefinition sourceStatement, boolean infer) {
        return LSFExClassSet.logical;
    }

    @Nullable
    public static LSFExClassSet resolveUnfriendValueClass(@NotNull LSFAggrPropertyDefinition sourceStatement, boolean infer) {
        return LSFExClassSet.toEx(resolveClass(sourceStatement.getCustomClassUsage()));
    }

    @Nullable
    public static LSFExClassSet resolveUnfriendValueClass(@NotNull LSFReflectionPropertyDefinition sourceStatement, boolean infer) {
        switch (LSFReflectionType.valueOf(sourceStatement.getReflectionPropertyType().getText())) {
            case CANONICALNAME:
                return new LSFExClassSet(CANONICAL_NAME_VALUE_CLASS);
            default:
                return null;
        }
    }

    // PropertyExpression.getValueClassNames

    public static List<String> getExpressionValueClassNames(@Nullable LSFExpression expression) {
        return expression == null ? Collections.<String>emptyList() : expression.getValueClassNames();
    }

    public static List<String> getValueClassNames(@NotNull LSFPropertyExpression sourceStatement) {
        return getValueClassNames(sourceStatement.getIfPE());
    }

    public static List<String> getValueClassNames(@NotNull LSFAliasedPropertyExpression sourceStatement) {
        return getValueClassNames(sourceStatement.getPropertyExpression());
    }

    public static List<String> getValueClassNames(@NotNull LSFIfPE sourceStatement) {
        return getValueClassNames(sourceStatement.getOrPEList().get(0));
    }

    public static List<String> getValueClassNames(@NotNull LSFOrPE sourceStatement) {
        List<LSFXorPE> xorPEList = sourceStatement.getXorPEList();
        if (xorPEList.size() == 1) {
            List<String> result = new ArrayList<>();
            result.addAll(getValueClassNames(xorPEList.get(0)));
            return result;
        }
        return singletonList(LogicalClass.instance.getName());
    }

    public static List<String> getValueClassNames(@NotNull LSFXorPE sourceStatement) {
        List<LSFAndPE> andPEList = sourceStatement.getAndPEList();
        if (andPEList.size() == 1) {
            List<String> result = new ArrayList<>();
            result.addAll(getValueClassNames(andPEList.get(0)));
            return result;
        }
        return singletonList(LogicalClass.instance.getName());
    }

    public static List<String> getValueClassNames(@NotNull LSFAndPE sourceStatement) {
        List<LSFNotPE> notPEList = sourceStatement.getNotPEList();
        if (notPEList.size() == 1) {
            List<String> result = new ArrayList<>();
            result.addAll(getValueClassNames(notPEList.get(0)));
            return result;
        }
        return singletonList(LogicalClass.instance.getName());
    }

    public static List<String> getValueClassNames(@NotNull LSFNotPE sourceStatement) {
        LSFEqualityPE equalityPE = sourceStatement.getEqualityPE();
        if (equalityPE != null) {
            return getValueClassNames(equalityPE);
        }
        return singletonList(LogicalClass.instance.getName());
    }

    public static List<String> getValueClassNames(@NotNull LSFEqualityPE sourceStatement) {
        List<LSFRelationalPE> relationalPEList = sourceStatement.getRelationalPEList();
        if (relationalPEList.size() == 2) {
            return singletonList(LogicalClass.instance.getName());
        }
        return getValueClassNames(relationalPEList.get(0));
    }

    public static List<String> getValueClassNames(@NotNull LSFRelationalPE sourceStatement) {
        List<LSFLikePE> likePEs = sourceStatement.getLikePEList();
        if (likePEs.size() == 2) {
            return singletonList(LogicalClass.instance.getName());
        }

        return getValueClassNames(likePEs.get(0));
    }

    public static List<String> getValueClassNames(@NotNull LSFLikePE sourceStatement) {
        List<String> result = new ArrayList<>();
        for (LSFAdditiveORPE addPE : sourceStatement.getAdditiveORPEList()) {
            result.addAll(getValueClassNames(addPE));
        }
        return result;
    }

    public static List<String> getValueClassNames(@NotNull LSFAdditiveORPE sourceStatement) {
        List<String> result = new ArrayList<>();
        for (LSFAdditivePE addPE : sourceStatement.getAdditivePEList()) {
            result.addAll(getValueClassNames(addPE));
        }
        return result;
    }

    public static List<String> getValueClassNames(@NotNull LSFAdditivePE sourceStatement) {
        List<String> result = new ArrayList<>();
        for (LSFMultiplicativePE multPE : sourceStatement.getMultiplicativePEList()) {
            result.addAll(getValueClassNames(multPE));
        }
        return result;
    }

    public static List<String> getValueClassNames(@NotNull LSFMultiplicativePE sourceStatement) {
        List<String> result = new ArrayList<>();
        for (LSFUnaryMinusPE umPE : sourceStatement.getUnaryMinusPEList()) {
            result.addAll(getValueClassNames(umPE));
        }
        return result;
    }

    public static List<String> getValueClassNames(@NotNull LSFUnaryMinusPE sourceStatement) {
        LSFUnaryMinusPE unaryMinusPE = sourceStatement.getUnaryMinusPE();
        if (unaryMinusPE != null) {
            return getValueClassNames(unaryMinusPE);
        }

        return getExpressionValueClassNames(sourceStatement.getPostfixUnaryPE());
    }

    public static List<String> getValueClassNames(@NotNull LSFPostfixUnaryPE sourceStatement) {
        LSFTypePropertyDefinition typePD = sourceStatement.getTypePropertyDefinition();
        if (typePD != null) {
            if (typePD.getTypeIs().getText().equals("IS")) {
                return singletonList(LogicalClass.instance.getName());
            } else {
                String typeDefClass = getClassName(typePD.getClassName());
                if (typeDefClass != null) {
                    return singletonList(typeDefClass);
                }
            }
        }

        return getValueClassNames(sourceStatement.getSimplePE());
    }

    public static List<String> getValueClassNames(@NotNull LSFSimplePE sourceStatement) {
        LSFPropertyExpression pe = sourceStatement.getPropertyExpression();
        if (pe != null) {
            return getValueClassNames(pe);
        }

        return getExpressionValueClassNames(sourceStatement.getExpressionPrimitive());
    }

    public static List<String> getValueClassNames(@NotNull LSFExpressionPrimitive sourceStatement) {
        LSFExprParameterUsage ep = sourceStatement.getExprParameterUsage();
        if (ep != null) {
            return getValueClassNames(ep);
        }
        return getExpressionValueClassNames(sourceStatement.getExpressionFriendlyPD());
    }

    private static List<String> getValueClassNames(LSFExprParameterUsage sourceStatement) {
        LSFExprParameterNameUsage exprParameterNameUsage = sourceStatement.getExprParameterNameUsage();
        if (exprParameterNameUsage == null) {
            return Collections.emptyList();
        }
        LSFResolveResult lsfResolveResult = exprParameterNameUsage.resolveNoCache();
        List<String> result = new ArrayList<>();
        for (LSFDeclaration decl : lsfResolveResult.declarations) {
            LSFStringClassRef className = ((LSFParamDeclare) decl).getClassName();
            if (className != null) {
                result.add(className.name);
            }
        }
        return result;
    }
    
    public static boolean isInline(@NotNull LSFMetaCodeStatement metaCodeStatement) {
        return metaCodeStatement.getMetaCodeStatementHeader().getMetaCodeStatementType().getText().equals("@@");
    }

    public static List<String> getValueClassNames(@Nullable LSFExpressionUnfriendlyPD sourceStatement) {
        if (sourceStatement != null) {
            return ((UnfriendlyPE) sourceStatement.getFirstChild()).getValueClassNames();
        }
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValueClassNames(@NotNull LSFDataPropertyDefinition sourceStatement) {
        List<String> result = new ArrayList<>();
        LSFClassName className = sourceStatement.getClassName();
        result.add(getClassName(className));
        return result;
    }

    public static List<String> getValueClassNames(@NotNull LSFAggrPropertyDefinition sourceStatement) {
        List<String> result = new ArrayList<>();
        LSFCustomClassUsage className = sourceStatement.getCustomClassUsage();
        result.add(className != null ? className.getName() : null);
        return result;
    }

    public static List<String> getValueClassNames(@NotNull LSFNativePropertyDefinition sourceStatement) {
        List<String> result = new ArrayList<>();
        LSFClassName className = sourceStatement.getClassName();
        result.add(getClassName(className));
        return result;
    }

    public static List<String> getValueClassNames(@NotNull LSFAbstractActionPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValueClassNames(@NotNull LSFCustomActionPropertyDefinitionBody sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValueClassNames(@NotNull LSFExternalActionPropertyDefinitionBody sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValueClassNames(@NotNull LSFAbstractPropertyDefinition sourceStatement) {
        List<String> result = new ArrayList<>();
        LSFClassName className = sourceStatement.getClassName();
        result.add(getClassName(className));
        return result;
    }

    public static List<String> getValueClassNames(@NotNull LSFFormulaPropertyDefinition sourceStatement) {
        @Nullable LSFTypedIdOrStringLiteralWithCheck typedIdOrStringLiteral = sourceStatement.getTypedIdOrStringLiteralWithCheck();
        if (typedIdOrStringLiteral != null) {
            return singletonList(typedIdOrStringLiteral.getBuiltInClassName().getName());
        }
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValueClassNames(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        LSFGroupPropertyBody body = sourceStatement.getGroupPropertyBody();
        if(body != null)
            return getValueClassNames(body.getNonEmptyPropertyExpressionList());
        return Collections.emptyList();
    }

    public static List<String> getValueClassNames(@NotNull LSFGroupExprPropertyDefinition sourceStatement) {
        LSFGroupPropertyBody body = sourceStatement.getGroupPropertyBody();
        if(body != null)
            return getValueClassNames(body.getNonEmptyPropertyExpressionList());
        return Collections.emptyList();
    }

    public static List<String> getValueClassNames(@NotNull LSFFilterPropertyDefinition sourceStatement) {
        return singletonList(LogicalClass.instance.getName());
    }

    public static List<String> getValueClassNames(@NotNull LSFReflectionPropertyDefinition sourceStatement) {
        switch (LSFReflectionType.valueOf(sourceStatement.getReflectionPropertyType().getText())) {
            case CANONICALNAME:
                return Collections.singletonList(CANONICAL_NAME_VALUE_CLASS.getName());
            default:
                return Collections.EMPTY_LIST;
        }
    }

    public static List<String> getValueClassNames(@NotNull LSFExpressionFriendlyPD sourceStatement) {
        return ((LSFExpression) sourceStatement.getFirstChild()).getValueClassNames();
    }

    public static List<String> getValueClassNames(@NotNull LSFJoinPropertyDefinition sourceStatement) {
        LSFPropertyUsage usage = sourceStatement.getPropertyUsage();
        if (usage != null) {
            return Collections.EMPTY_LIST;
        }

        LSFPropertyExprObject exprObject = sourceStatement.getPropertyExprObject();

        assert exprObject != null;

        LSFPropertyCalcStatement pCalcStatement = exprObject.getPropertyCalcStatement();
        return getValueAPClassNames(pCalcStatement); //exprObject.getActionUnfriendlyPD(), exprObject.getListActionPropertyDefinitionBody());
    }

    public static List<String> getValueAPClassNames(LSFPropertyCalcStatement pCalcStatement) {
        if(pCalcStatement == null)
            return Collections.EMPTY_LIST;

        LSFPropertyExpression pe = pCalcStatement.getPropertyExpression();
        if (pe != null) {
            return getValueClassNames(pe);
        }
        return getValueClassNames(pCalcStatement.getExpressionUnfriendlyPD());
    }

    @Nullable
    private static List<String> getValueClassNames(List<LSFPropertyExpression> list) {
        if (list.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        List<String> result = null;
        for (int i = 0; i < list.size(); i++) {
            List<String> valueClass = getValueClassNames(list.get(i));
            if (i == 0) {
                result = valueClass;
            } else {
                result.addAll(valueClass);
            }
        }
        return result;
    }

    private static List<String> getValueClassNames(@Nullable LSFNonEmptyPropertyExpressionList list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return getValueClassNames(list.getPropertyExpressionList());
    }

    @Nullable
    private static List<String> getAliasedValueClassNames(List<LSFAliasedPropertyExpression> list) {
        if (list.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        List<String> result = null;
        for (int i = 0; i < list.size(); i++) {
            List<String> valueClass = getValueClassNames(list.get(i));
            if (i == 0) {
                result = valueClass;
            } else {
                result.addAll(valueClass);
            }
        }
        return result;
    }

    private static List<String> getValueClassNames(@Nullable LSFNonEmptyAliasedPropertyExpressionList list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return getAliasedValueClassNames(list.getAliasedPropertyExpressionList());
    }

    public static List<String> getValueClassNames(@NotNull LSFMultiPropertyDefinition sourceStatement) {
        return getValueClassNames(sourceStatement.getNonEmptyPropertyExpressionList());
    }

    public static List<String> getValueClassNames(@NotNull LSFOverridePropertyDefinition sourceStatement) {
        return getValueClassNames(sourceStatement.getNonEmptyPropertyExpressionList());
    }

    public static List<String> getValueClassNames(@NotNull LSFIfElsePropertyDefinition sourceStatement) {
        List<LSFPropertyExpression> peList = sourceStatement.getPropertyExpressionList();
        if (peList.isEmpty()) {
            return Collections.emptyList();
        }
        return getValueClassNames(peList.subList(1, peList.size()));
    }

    public static List<String> getValueClassNames(@NotNull LSFMaxPropertyDefinition sourceStatement) {
        return getValueClassNames(sourceStatement.getNonEmptyPropertyExpressionList());
    }

    public static List<String> getValueClassNames(@NotNull LSFCasePropertyDefinition sourceStatement) {
        List<LSFPropertyExpression> list = new ArrayList<>();
        for (LSFCaseBranchBody caseBranch : sourceStatement.getCaseBranchBodyList()) {
            List<LSFPropertyExpression> propertyExpressionList = caseBranch.getPropertyExpressionList();
            if (propertyExpressionList.size() > 1) {
                list.add(propertyExpressionList.get(1));
            }
        }
        LSFPropertyExpression elseExpr = sourceStatement.getPropertyExpression();
        if (elseExpr != null) {
            list.add(elseExpr);
        }
        return getValueClassNames(list);
    }

    public static List<String> getValueClassNames(@NotNull LSFPartitionPropertyDefinition sourceStatement) {
        return getExpressionValueClassNames(sourceStatement.getPropertyExpression());
    }

    public static List<String> getValueClassNames(@NotNull LSFRecursivePropertyDefinition sourceStatement) {
        return getValueClassNames(sourceStatement.getPropertyExpressionList());
    }

    public static List<String> getValueClassNames(@NotNull LSFStructCreationPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValueClassNames(@NotNull LSFConcatPropertyDefinition sourceStatement) {
        return getValueClassNames(sourceStatement.getNonEmptyPropertyExpressionList());
    }

    public static List<String> getValueClassNames(@NotNull LSFJsonPropertyDefinition sourceStatement) {
        return getValueClassNames(sourceStatement.getNonEmptyAliasedPropertyExpressionList());
    }

    public static List<String> getValueClassNames(@NotNull LSFJsonFormPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValueClassNames(@NotNull LSFCastPropertyDefinition sourceStatement) {
        return singletonList(sourceStatement.getBuiltInClassName().getName());
    }

    public static List<String> getValueClassNames(@NotNull LSFSessionPropertyDefinition sourceStatement) {
        LSFSessionPropertyType type = sourceStatement.getSessionPropertyType();
        if (type.getText().equals("PREV")) {
            return getValueClassNames(sourceStatement.getPropertyExpression());
        }
        return singletonList(LogicalClass.instance.getName());
    }

    public static List<String> getValueClassNames(@NotNull LSFSignaturePropertyDefinition sourceStatement) {
        return singletonList(LogicalClass.instance.getName());
    }

    public static List<String> getValueClassNames(@NotNull LSFActiveTabPropertyDefinition sourceStatement) {
        return singletonList(LogicalClass.instance.getName());
    }

    public static List<String> getValueClassNames(@NotNull LSFRoundPropertyDefinition sourceStatement) {
        return singletonList(LogicalClass.instance.getName());
    }

    public static List<String> getValueClassNames(@NotNull LSFExpressionLiteral sourceStatement) {
        DataClass builtInClass = resolveBuiltInValueClass(sourceStatement);
        if (builtInClass != null) {
            return singletonList(builtInClass.getName());
        }
        LSFStaticObjectID staticObjectID = sourceStatement.getStaticObjectID();
        if (staticObjectID != null) {
            return singletonList(staticObjectID.getCustomClassUsage().getName());
        }
        return Collections.EMPTY_LIST;
    }


    // PropertyExpression.getValuePropertyNames

    public static List<String> getExpressionValuePropertyNames(@Nullable LSFExpression expression) {
        return expression == null ? Collections.<String>emptyList() : expression.getValuePropertyNames();
    }

    public static List<String> getValuePropertyNames(@NotNull LSFPropertyExpression sourceStatement) {
        return getValuePropertyNames(sourceStatement.getIfPE());
    }

    public static List<String> getValuePropertyNames(@NotNull LSFAliasedPropertyExpression sourceStatement) {
        return getValuePropertyNames(sourceStatement.getPropertyExpression());
    }

    public static List<String> getValuePropertyNames(@NotNull LSFIfPE sourceStatement) {
        return getValuePropertyNames(sourceStatement.getOrPEList().get(0));
    }

    public static List<String> getValuePropertyNames(@NotNull LSFOrPE sourceStatement) {
        List<String> result = new ArrayList<>();
        List<LSFXorPE> xorPEList = sourceStatement.getXorPEList();
        if (xorPEList.size() == 1) {
            result.addAll(getValuePropertyNames(xorPEList.get(0)));
        }
        return result;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFXorPE sourceStatement) {
        List<String> result = new ArrayList<>();
        List<LSFAndPE> andPEList = sourceStatement.getAndPEList();
        if (andPEList.size() == 1) {
            result.addAll(getValuePropertyNames(andPEList.get(0)));
        }
        return result;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFAndPE sourceStatement) {
        List<String> result = new ArrayList<>();
        List<LSFNotPE> notPEList = sourceStatement.getNotPEList();
        if (notPEList.size() == 1) {
            result.addAll(getValuePropertyNames(notPEList.get(0)));
        }
        return result;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFNotPE sourceStatement) {
        LSFEqualityPE equalityPE = sourceStatement.getEqualityPE();
        if (equalityPE != null) {
            return getValuePropertyNames(equalityPE);
        }
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFEqualityPE sourceStatement) {
        List<LSFRelationalPE> relationalPEList = sourceStatement.getRelationalPEList();
        if (relationalPEList.size() == 2) {
            return Collections.EMPTY_LIST;
        }
        return getValuePropertyNames(relationalPEList.get(0));
    }

    public static List<String> getValuePropertyNames(@NotNull LSFRelationalPE sourceStatement) {
        List<LSFLikePE> likePEs = sourceStatement.getLikePEList();
        if (likePEs.size() == 2) {
            return Collections.EMPTY_LIST;
        }

        return getValuePropertyNames(likePEs.get(0));
    }

    public static List<String> getValuePropertyNames(@NotNull LSFLikePE sourceStatement) {
        List<String> result = new ArrayList<>();
        for (LSFAdditiveORPE addORPE : sourceStatement.getAdditiveORPEList()) {
            result.addAll(getValuePropertyNames(addORPE));
        }
        return result;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFAdditiveORPE sourceStatement) {
        List<String> result = new ArrayList<>();
        for (LSFAdditivePE addPE : sourceStatement.getAdditivePEList()) {
            result.addAll(getValuePropertyNames(addPE));
        }
        return result;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFAdditivePE sourceStatement) {
        List<String> result = new ArrayList<>();
        for (LSFMultiplicativePE multPE : sourceStatement.getMultiplicativePEList()) {
            result.addAll(getValuePropertyNames(multPE));
        }
        return result;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFMultiplicativePE sourceStatement) {
        List<String> result = new ArrayList<>();
        for (LSFUnaryMinusPE umPE : sourceStatement.getUnaryMinusPEList()) {
            result.addAll(getValuePropertyNames(umPE));
        }
        return result;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFUnaryMinusPE sourceStatement) {
        LSFUnaryMinusPE unaryMinusPE = sourceStatement.getUnaryMinusPE();
        if (unaryMinusPE != null) {
            return getValuePropertyNames(unaryMinusPE);
        }

        return getExpressionValuePropertyNames(sourceStatement.getPostfixUnaryPE());
    }

    public static List<String> getValuePropertyNames(@NotNull LSFPostfixUnaryPE sourceStatement) {
        LSFTypePropertyDefinition typePD = sourceStatement.getTypePropertyDefinition();
        if (typePD != null) {
            return Collections.EMPTY_LIST;
        }

        return getValuePropertyNames(sourceStatement.getSimplePE());
    }

    public static List<String> getValuePropertyNames(@NotNull LSFSimplePE sourceStatement) {
        LSFPropertyExpression pe = sourceStatement.getPropertyExpression();
        if (pe != null) {
            return getValuePropertyNames(pe);
        }

        return getExpressionValuePropertyNames(sourceStatement.getExpressionPrimitive());
    }

    public static List<String> getValuePropertyNames(@NotNull LSFExpressionPrimitive sourceStatement) {
        LSFExprParameterUsage ep = sourceStatement.getExprParameterUsage();
        if (ep != null) {
            return getValuePropertyNames(ep);
        }

        return getExpressionValuePropertyNames(sourceStatement.getExpressionFriendlyPD());
    }

    private static List<String> getValuePropertyNames(LSFExprParameterUsage sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@Nullable LSFExpressionUnfriendlyPD sourceStatement) {
        if (sourceStatement != null) {
            return ((UnfriendlyPE) sourceStatement.getFirstChild()).getValuePropertyNames();
        }
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFDataPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFNativePropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFAbstractActionPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFCustomActionPropertyDefinitionBody sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFExternalActionPropertyDefinitionBody sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFAbstractPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFFormulaPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFGroupExprPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFFilterPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFAggrPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFReflectionPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFExpressionFriendlyPD sourceStatement) {
        return ((LSFExpression) sourceStatement.getFirstChild()).getValuePropertyNames();
    }

    public static List<String> getValuePropertyNames(@NotNull LSFJoinPropertyDefinition sourceStatement) {
        LSFPropertyUsage usage = sourceStatement.getPropertyUsage();
        if (usage != null) {
            return singletonList(usage.getName());
        }

        LSFPropertyExprObject exprObject = sourceStatement.getPropertyExprObject();

        assert exprObject != null;

        return getValueAPPropertyNames(exprObject.getPropertyCalcStatement()); //exprObject.getActionUnfriendlyPD(), exprObject.getListActionPropertyDefinitionBody());
    }

    public static List<String> getValueAPPropertyNames(LSFPropertyCalcStatement pCalcStatement) {
        if(pCalcStatement == null)
            return Collections.EMPTY_LIST;

        LSFPropertyExpression pe = pCalcStatement.getPropertyExpression();
        if (pe != null) {
            return getValuePropertyNames(pe);
        }
        return getValuePropertyNames(pCalcStatement.getExpressionUnfriendlyPD());
    }

    private static List<String> getValuePropertyNames(List<LSFPropertyExpression> list) {
        if (list.size() == 0)
            return Collections.EMPTY_LIST;

        List<String> result = null;
        for (int i = 0; i < list.size(); i++) {
            List<String> valueProperty = getValuePropertyNames(list.get(i));
            if (i == 0) {
                result = valueProperty;
            } else {
                result.addAll(valueProperty);
            }
        }
        return result;
    }

    private static List<String> getValuePropertyNames(@Nullable LSFNonEmptyPropertyExpressionList list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return getValuePropertyNames(list.getPropertyExpressionList());
    }

    private static List<String> getAliasedValuePropertyNames(List<LSFAliasedPropertyExpression> list) {
        if (list.size() == 0)
            return Collections.EMPTY_LIST;

        List<String> result = null;
        for (int i = 0; i < list.size(); i++) {
            List<String> valueProperty = getValuePropertyNames(list.get(i));
            if (i == 0) {
                result = valueProperty;
            } else {
                result.addAll(valueProperty);
            }
        }
        return result;
    }

    private static List<String> getValuePropertyNames(@Nullable LSFNonEmptyAliasedPropertyExpressionList list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return getAliasedValuePropertyNames(list.getAliasedPropertyExpressionList());
    }

    public static List<String> getValuePropertyNames(@NotNull LSFMultiPropertyDefinition sourceStatement) {
        return getValuePropertyNames(sourceStatement.getNonEmptyPropertyExpressionList());
    }

    public static List<String> getValuePropertyNames(@NotNull LSFOverridePropertyDefinition sourceStatement) {
        return getValuePropertyNames(sourceStatement.getNonEmptyPropertyExpressionList());
    }

    public static List<String> getValuePropertyNames(@NotNull LSFIfElsePropertyDefinition sourceStatement) {
        List<LSFPropertyExpression> peList = sourceStatement.getPropertyExpressionList();
        if (peList.isEmpty()) {
            return Collections.emptyList();
        }
        return getValuePropertyNames(peList.subList(1, peList.size()));
    }

    public static List<String> getValuePropertyNames(@NotNull LSFMaxPropertyDefinition sourceStatement) {
        return getValuePropertyNames(sourceStatement.getNonEmptyPropertyExpressionList());
    }

    public static List<String> getValuePropertyNames(@NotNull LSFCasePropertyDefinition sourceStatement) {
        List<LSFPropertyExpression> list = new ArrayList<>();
        for (LSFCaseBranchBody caseBranch : sourceStatement.getCaseBranchBodyList()) {
            List<LSFPropertyExpression> propertyExpressionList = caseBranch.getPropertyExpressionList();
            if (propertyExpressionList.size() > 1) {
                list.add(propertyExpressionList.get(1));
            }
        }
        LSFPropertyExpression elseExpr = sourceStatement.getPropertyExpression();
        if (elseExpr != null) {
            list.add(elseExpr);
        }
        return getValuePropertyNames(list);
    }

    public static List<String> getValuePropertyNames(@NotNull LSFPartitionPropertyDefinition sourceStatement) {
        return getExpressionValuePropertyNames(sourceStatement.getPropertyExpression());
    }

    public static List<String> getValuePropertyNames(@NotNull LSFRecursivePropertyDefinition sourceStatement) {
        return getValuePropertyNames(sourceStatement.getPropertyExpressionList());
    }

    public static List<String> getValuePropertyNames(@NotNull LSFStructCreationPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFConcatPropertyDefinition sourceStatement) {
        return getValuePropertyNames(sourceStatement.getNonEmptyPropertyExpressionList());
    }

    public static List<String> getValuePropertyNames(@NotNull LSFJsonPropertyDefinition sourceStatement) {
        return getValuePropertyNames(sourceStatement.getNonEmptyAliasedPropertyExpressionList());
    }

    public static List<String> getValuePropertyNames(@NotNull LSFJsonFormPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFCastPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFSessionPropertyDefinition sourceStatement) {
        LSFSessionPropertyType type = sourceStatement.getSessionPropertyType();
        if (type.getText().equals("PREV")) {
            return getValuePropertyNames(sourceStatement.getPropertyExpression());
        }
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFSignaturePropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFActiveTabPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFRoundPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFExpressionLiteral sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    @NotNull
    public static Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(@NotNull UnfriendlyPE pe, PsiElement singleElement, List<LSFParamDeclaration> declareParams) {
        int size = pe.resolveValueParamClasses(declareParams).size();
        List<PsiElement> elements = new ArrayList<>();
        for(int i=0;i<size;i++)
            elements.add(singleElement);
        return checkValueParamClasses(pe, elements, declareParams);
    }
    @NotNull
    public static Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(@NotNull UnfriendlyPE pe, LSFClassNameList classNameList, List<LSFParamDeclaration> declareParams) {
        List<PsiElement> elements = Collections.emptyList();
        if (classNameList != null) {
            LSFNonEmptyClassNameList ne = classNameList.getNonEmptyClassNameList();
            if (ne != null)
                elements = BaseUtils.immutableCast(ne.getClassNameList());
        }
        return checkValueParamClasses(pe, elements, declareParams); 
    }
    
    @NotNull
    public static Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(@NotNull UnfriendlyPE pe, List<? extends PsiElement> elements, List<LSFParamDeclaration> declareParams) {
        List<LSFParamDeclaration> incorrectParams = new ArrayList<>();
        Map<PsiElement, Pair<LSFClassSet, LSFClassSet>> incorrectBys = new HashMap<>();

        List<LSFClassSet> declareClasses = resolveParamDeclClasses(declareParams);
        List<LSFClassSet> valueClasses = LSFExClassSet.fromEx(pe.resolveValueParamClasses(declareParams));
        assert elements.size() == valueClasses.size();
        for(int i=0,size=declareClasses.size();i<size;i++) {
            LSFClassSet declareClass = declareClasses.get(i);
            if(i >= valueClasses.size()) {
                incorrectParams.add(declareParams.get(i));
            } else {
                LSFClassSet valueClass = valueClasses.get(i);
                if (declareClass != null && valueClass != null && !declareClass.isCompatible(valueClass)) {
                    incorrectBys.put(elements.get(i), new Pair<>(valueClass, declareClass));
                }
            }
        }
        
        for(int i=declareClasses.size();i<valueClasses.size();i++)
            incorrectBys.put(elements.get(i), null);

        return new Pair<>(incorrectParams, incorrectBys);
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFExpressionUnfriendlyPD sourceStatement, List<LSFParamDeclaration> declareParams) {
        return ((UnfriendlyPE) sourceStatement.getChildren()[0]).resolveValueParamClasses(declareParams);
    }

    @NotNull
    public static Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(@NotNull LSFExpressionUnfriendlyPD sourceStatement, List<LSFParamDeclaration> declareParams) {
        return ((UnfriendlyPE) sourceStatement.getChildren()[0]).checkValueParamClasses(declareParams);
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(LSFActionUnfriendlyPD unfriendlyPD, LSFListActionPropertyDefinitionBody listBody, List<LSFParamDeclaration> declareParams) {
        if(unfriendlyPD != null)
            return LSFPsiImplUtil.resolveValueParamClasses(unfriendlyPD, declareParams);
        return null;
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFActionUnfriendlyPD actionDef, List<LSFParamDeclaration> declareParams) {
        return ((UnfriendlyPE) actionDef.getChildren()[0]).resolveValueParamClasses(declareParams);
    }

    @NotNull
    public static Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(@NotNull LSFActionUnfriendlyPD sourceStatement, List<LSFParamDeclaration> declareParams) {
        return ((UnfriendlyPE) sourceStatement.getChildren()[0]).checkValueParamClasses(declareParams);
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFDataPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        return LSFExClassSet.toEx(resolveClasses(sourceStatement.getClassNameList()));
    }

    @NotNull
    public static Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(@NotNull LSFDataPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        if((sourceStatement.getClassNameList() == null)) {
            return Pair.create(new ArrayList<>(), new HashMap<>());
        } else {
            return checkValueParamClasses(sourceStatement, sourceStatement.getClassNameList(), declareParams);
        }
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFNativePropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        return LSFExClassSet.toEx(resolveClasses(sourceStatement.getClassNameList()));
    }

    @NotNull
    public static Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(@NotNull LSFNativePropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        return checkValueParamClasses(sourceStatement, sourceStatement.getClassNameList(), declareParams);
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFAbstractPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        return LSFExClassSet.toEx(resolveClasses(sourceStatement.getClassNameList()));
    }

    @NotNull
    public static Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(@NotNull LSFAbstractPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        if((sourceStatement.getClassNameList() == null)) {
            return Pair.create(new ArrayList<>(), new HashMap<>());
        } else {
            return checkValueParamClasses(sourceStatement, sourceStatement.getClassNameList(), declareParams);
        }
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFAbstractActionPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        return LSFExClassSet.toEx(resolveClasses(sourceStatement.getClassNameList()));
    }

    @NotNull
    public static Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(@NotNull LSFAbstractActionPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        return checkValueParamClasses(sourceStatement, sourceStatement.getClassNameList(), declareParams);
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFCustomActionPropertyDefinitionBody sourceStatement, List<LSFParamDeclaration> declareParams) {
        LSFBracketedClassNameList bracketedClassNameList = sourceStatement.getBracketedClassNameList();
        if (bracketedClassNameList != null)
            return LSFExClassSet.toEx(resolveClasses(bracketedClassNameList.getClassNameList()));

        return null;
    }

    @NotNull
    public static Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(@NotNull LSFCustomActionPropertyDefinitionBody sourceStatement, List<LSFParamDeclaration> declareParams) {
        LSFBracketedClassNameList bracketedClassNameList = sourceStatement.getBracketedClassNameList();
        if (bracketedClassNameList != null)
            return checkValueParamClasses(sourceStatement, bracketedClassNameList.getClassNameList(), declareParams);
        return new Pair<>(Collections.emptyList(), Collections.emptyMap());
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFGroupPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        LSFGroupPropertyBy groupBy = sourceStatement.getGroupPropertyBy();
        if (groupBy == null)
            return new ArrayList<>();
        
        List<LSFExClassSet> groupProps = resolveParamClasses(groupBy.getNonEmptyPropertyExpressionList());
        if(declareParams == null)
            return groupProps;

        Set<String> usedInterfaces = new ExprsContextModifier(getContextExprs(sourceStatement)).resolveAllParams();
//        нужно groupProps в дырки вставить для context independent группировки
        int ga = 0;
        int groupSize = groupProps.size();
        List<LSFExClassSet> allGroupProps = new ArrayList<>();
        for (LSFParamDeclaration declareParam : declareParams) {

            LSFExClassSet add;
            if (ga >= groupSize || usedInterfaces.contains(declareParam.getDeclName()))
                // тут тоже возможно нужна проверка на соответствие классов
                add = null;
            else
                add = groupProps.get(ga++);
            allGroupProps.add(add);
        }   

        return allGroupProps;
    }
    
    public static Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(@NotNull LSFGroupPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        LSFGroupPropertyBy groupBy = sourceStatement.getGroupPropertyBy();
        assert groupBy != null;

        List<LSFParamDeclaration> incorrectParams = new ArrayList<>();
        Map<PsiElement, Pair<LSFClassSet, LSFClassSet>> incorrectBys = new HashMap<>();

        LSFNonEmptyPropertyExpressionList groupExprsList = groupBy.getNonEmptyPropertyExpressionList();
        List<LSFPropertyExpression> groupExprs = groupExprsList == null ? Collections.emptyList() : groupExprsList.getPropertyExpressionList(); 
        List<LSFClassSet> groupClasses = finishParamClasses(groupExprsList);
        List<LSFClassSet> declareClasses = resolveParamDeclClasses(declareParams);

        Set<String> usedInterfaces = new ExprsContextModifier(getContextExprs(sourceStatement)).resolveAllParams();
//        нужно groupProps в дырки вставить для context independent группировки
        int ga = 0;
        int groupSize = groupClasses.size();
        for (int i = 0; i < declareParams.size(); i++) {
            LSFParamDeclaration declareParam = declareParams.get(i);

            boolean usedParam = usedInterfaces.contains(declareParam.getDeclName());
            if (ga >= groupSize || usedParam) {
                if (!usedParam)
                    incorrectParams.add(declareParam);
            } else {
                LSFClassSet groupClass = groupClasses.get(ga);
                LSFClassSet declareClass = declareClasses.get(i);
                if (declareClass != null && groupClass != null && !declareClass.isCompatible(groupClass)) {
                    incorrectBys.put(groupExprs.get(ga), new Pair<>(groupClass, declareClass));
                }
                ga++;
            }
        }
        
        for(;ga<groupSize;ga++)
            incorrectBys.put(groupExprs.get(ga), null);

        return new Pair<>(incorrectParams, incorrectBys);
    }

    @NotNull
    public static List<LSFExClassSet> inferGroupValueParamClasses(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        LSFGroupPropertyBy groupBy = sourceStatement.getGroupPropertyBy();
        if (groupBy == null)
            return new ArrayList<>();

        return resolveParamClasses(groupBy.getNonEmptyPropertyExpressionList(), inferGroupParamClasses(sourceStatement));
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFFormulaPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        List<LSFStringLiteral> stringLiteralList = new ArrayList<>();
        LSFFormulaPropertySyntaxList formulaPropertySyntaxList = sourceStatement.getFormulaPropertySyntaxList();
        if (formulaPropertySyntaxList != null) {
            for (LSFFormulaPropertySyntax syntax : formulaPropertySyntaxList.getFormulaPropertySyntaxList()) {
                stringLiteralList.add(syntax.getStringLiteral());
            }
        }
            
        if (stringLiteralList.isEmpty()) {
            return null;
        }
        LSFStringLiteral stringLiteral = stringLiteralList.get(0);
        if (stringLiteral == null) {
            return null;
        }

        String text = stringLiteral.getText();
        int i = 0;
        List<LSFExClassSet> result = new ArrayList<>();
        // todo [dale]: '$' could be escaped in newest versions and simple text search could be wrong in some cases ('\$1')
        while (text.contains("$" + (i + 1))) {
            i++;
            result.add(null);
        }
        return result;
    }

    @NotNull
    public static Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(@NotNull LSFFormulaPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        Map<LSFStringLiteral, Set<Integer>> implementationsParams = getFormulaParams(sourceStatement);
        
        List<LSFParamDeclaration> unusedDeclarations = new ArrayList<>();
        for (int paramIndex = 0; paramIndex < declareParams.size(); ++paramIndex) {
            for (Set<Integer> implParameters : implementationsParams.values()) {
                if (!implParameters.contains(paramIndex+1)) {
                    unusedDeclarations.add(declareParams.get(paramIndex));
                    break;
                }
            }
        }

        Map<PsiElement, Pair<LSFClassSet, LSFClassSet>> wrongImplementations = new HashMap<>();
        for (Map.Entry<LSFStringLiteral, Set<Integer>> entry : implementationsParams.entrySet()) {
            Set<Integer> usedParameters = entry.getValue();
            if (!usedParameters.isEmpty() && (Collections.max(usedParameters) > declareParams.size() || Collections.min(usedParameters) < 1)) {
                wrongImplementations.put(entry.getKey(), null);
            }
        }
        return new Pair<>(unusedDeclarations, wrongImplementations);
    }

    private static Map<LSFStringLiteral, Set<Integer>> getFormulaParams(LSFFormulaPropertyDefinition definition) {
        Map<LSFStringLiteral, Set<Integer>> params = new HashMap<>();
        if (definition.getFormulaPropertySyntaxList() != null) {
            for (LSFFormulaPropertySyntax syntax : definition.getFormulaPropertySyntaxList().getFormulaPropertySyntaxList()) {
                LSFStringLiteral literal = syntax.getStringLiteral();
                params.put(literal, new HashSet<>());
                String code = syntax.getStringLiteral().getText();
                if (code != null) {
                    // todo [dale]: '$' could be escaped in newest versions and this simple regex could be wrong in some cases ('\$1')
                    Pattern pattern = Pattern.compile("\\$\\d+");
                    Matcher matcher = pattern.matcher(code);
                    while (matcher.find()) {
                        String group = matcher.group();
                        int paramIndex = Integer.parseInt(group.substring(1));
                        params.get(literal).add(paramIndex);
                    }
                }
            }
        }
        return params; 
    }
    
    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFFilterPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        LSFGroupObjectUsage groupObjectUsage = sourceStatement.getGroupObjectID().getGroupObjectUsage();
        if (groupObjectUsage != null) {
            return LSFExClassSet.toEx(groupObjectUsage.resolveClasses());
        }
        return null;
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFAggrPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        LSFPropertyExpression pe = sourceStatement.getPropertyExpression();
        if(pe != null)
            return LSFExClassSet.toEx(resolveValueParamClasses(pe, declareParams));
        return Collections.emptyList();
    }

    @NotNull
    public static Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(@NotNull LSFFilterPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        LSFGroupObjectUsage groupObjectUsage = sourceStatement.getGroupObjectID().getGroupObjectUsage();
        if (groupObjectUsage != null) {
            return checkValueParamClasses(sourceStatement, groupObjectUsage, declareParams);
        }
        return new Pair<>(Collections.emptyList(), Collections.emptyMap());
    }

    @NotNull
    public static Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(@NotNull LSFAggrPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        LSFPropertyExpression pe = sourceStatement.getPropertyExpression();
        return checkValueParamClasses(sourceStatement, pe != null ? pe.resolveParams() : Collections.emptyList(), declareParams);
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFReflectionPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        return null;
    }

    @NotNull
    public static Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> checkValueParamClasses(@NotNull LSFReflectionPropertyDefinition sourceStatement, List<LSFParamDeclaration> declareParams) {
        return new Pair<>(Collections.emptyList(), Collections.emptyMap());
    }

    // UnfriendlyPE.getValueParamClasses

    public static LSFExplicitClasses getValueParamClassNames(@NotNull LSFExpressionUnfriendlyPD sourceStatement) {
        return ((UnfriendlyPE) sourceStatement.getFirstChild()).getValueParamClassNames();
    }

    public static LSFExplicitClasses getValueParamClassNames(@NotNull LSFActionUnfriendlyPD sourceStatement) {
        return ((UnfriendlyPE) sourceStatement.getFirstChild()).getValueParamClassNames();
    }

    public static LSFExplicitClasses getValueParamClassNames(@NotNull LSFDataPropertyDefinition sourceStatement) {
        return getClassNameRefs(sourceStatement.getClassNameList());
    }

    public static LSFExplicitClasses getValueParamClassNames(@NotNull LSFNativePropertyDefinition sourceStatement) {
        return getClassNameRefs(sourceStatement.getClassNameList());
    }

    public static LSFExplicitClasses getValueParamClassNames(@NotNull LSFAbstractActionPropertyDefinition sourceStatement) {
        return getClassNameRefs(sourceStatement.getClassNameList());
    }

    @Nullable
    public static LSFExplicitClasses getValueParamClassNames(@NotNull LSFCustomActionPropertyDefinitionBody sourceStatement) {
        LSFBracketedClassNameList bracketedClassNameList = sourceStatement.getBracketedClassNameList();
        if (bracketedClassNameList != null)
            return getClassNameRefs(bracketedClassNameList.getClassNameList());
        return null;
    }

    @Nullable
    public static LSFExplicitClasses getValueParamClassNames(@NotNull LSFExternalActionPropertyDefinitionBody sourceStatement) {
        return null;
    }

    public static LSFExplicitClasses getValueParamClassNames(@NotNull LSFAbstractPropertyDefinition sourceStatement) {
        return getClassNameRefs(sourceStatement.getClassNameList());
    }

    public static LSFExplicitClasses getValueParamClassNames(@NotNull LSFFormulaPropertyDefinition sourceStatement) {
        return null;
    }

    public static LSFExplicitClasses getValueParamClassNames(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        LSFGroupPropertyBy groupPropertyBy = sourceStatement.getGroupPropertyBy();
        if (groupPropertyBy != null) {
            Set<String> valueClasses = new HashSet<>();
            LSFNonEmptyPropertyExpressionList neList = groupPropertyBy.getNonEmptyPropertyExpressionList();
            if(neList != null) {
                for (LSFPropertyExpression pe : neList.getPropertyExpressionList()) {
                    valueClasses.addAll(getValueClassNames(pe));
                }
            }
            return LSFImplicitExplicitClasses.create(valueClasses);
        }
        return new LSFExplicitSignature(Collections.EMPTY_LIST);
    }

    public static LSFExplicitClasses getValueParamClassNames(@NotNull LSFFilterPropertyDefinition sourceStatement) {
        return null;
    }

    public static LSFExplicitClasses getValueParamClassNames(@NotNull LSFAggrPropertyDefinition sourceStatement) {
        LSFPropertyExpression pe = sourceStatement.getPropertyExpression();
        if(pe != null)
            return LSFPsiImplUtil.getClassNameRefs(BaseUtils.<List<LSFParamDeclaration>>immutableCast(pe.resolveParams()));
        return null;
    }

    public static LSFExplicitClasses getValueParamClassNames(@NotNull LSFReflectionPropertyDefinition sourceStatement) {
        return null;
    }

    @NotNull
    public static LSFExplicitSignature getClassNameRefs(@Nullable LSFClassNameList classNameList) {
        if (classNameList == null) {
            return new LSFExplicitSignature(Collections.EMPTY_LIST);
        }
        LSFNonEmptyClassNameList ne = classNameList.getNonEmptyClassNameList();
        if (ne == null) {
            return new LSFExplicitSignature(Collections.EMPTY_LIST);
        }

        List<LSFStringClassRef> result = new ArrayList<>();
        for (LSFClassName className : ne.getClassNameList()) {
            result.add(getClassNameRef(className));
        }
        return new LSFExplicitSignature(result);
    }

    @NotNull
    public static LSFExplicitSignature getClassNameRefs(List<LSFParamDeclaration> params) {
        List<LSFStringClassRef> classNames = new ArrayList<>();
        for (LSFParamDeclaration param : params) {
            classNames.add(param.getClassName());
        }
        return new LSFExplicitSignature(classNames);
    }


    @Nullable
    public static String getClassName(@Nullable LSFClassName className) {
        if (className == null) {
            return null;
        }

        LSFBuiltInClassName builtInClassName = className.getBuiltInClassName();
        if (builtInClassName != null) {
            return builtInClassName.getName();
        }
        LSFCustomClassUsage customClassUsage = className.getCustomClassUsage();
        
        assert customClassUsage != null;
        
        return customClassUsage.getName();
    }

    @Nullable
    public static LSFStringClassRef getClassNameRef(@Nullable LSFClassName className) {
        if (className == null) {
            return null;
        }
        LSFBuiltInClassName builtInClassName = className.getBuiltInClassName();
        if (builtInClassName != null) {
            return new LSFStringClassRef(builtInClassName.getName());
        }
        LSFCustomClassUsage customClassUsage = className.getCustomClassUsage();

        assert customClassUsage != null;
        return getClassNameRef(customClassUsage);
    }

    public static LSFStringClassRef getClassNameRef(@NotNull  LSFCustomClassUsage customClassUsage) {
        return new LSFStringClassRef(customClassUsage.getFullNameRef(),  customClassUsage.getName());
    }

    // LSFPropertyExpression.resolveValueParamClasses
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFPropertyExpression sourceStatement, List<LSFParamDeclaration> declareParams) {
        List<LSFClassSet> result = new ArrayList<>();
        for (LSFExprParamDeclaration param : sourceStatement.resolveParams())
            result.add(param.resolveClass());
        return result;
    }

    // по идее должен вызываться если подразумевается отсутствие внешнего контекста
    @NotNull
    public static List<LSFExprParamDeclaration> resolveParams(@NotNull LSFPropertyExpression sourceStatement) {
        return new ExprsContextModifier(sourceStatement).resolveParams(Integer.MAX_VALUE, new HashSet<>());
    }

    @NotNull
    public static Set<String> resolveAllParams(@NotNull LSFPropertyExpression sourceStatement) {
        return new ExprsContextModifier(sourceStatement).resolveAllParams();
    }

    @NotNull
    public static Set<String> resolveAllParams(@NotNull LSFListActionPropertyDefinitionBody sourceStatement) {
        return new ActionContextModifier(sourceStatement).resolveAllParams();
    }

    public static List<LSFParamDeclaration> resolveParams(@Nullable LSFClassParamDeclareList classNameList) {
        if (classNameList == null)
            return null;

        List<LSFParamDeclaration> result = new ArrayList<>();

        LSFNonEmptyClassParamDeclareList ne = classNameList.getNonEmptyClassParamDeclareList();
        if (ne == null)
            return result;
        for (LSFClassParamDeclare classParamDeclare : ne.getClassParamDeclareList())
            result.add(classParamDeclare.getParamDeclare());
        return result;
    }

    public static List<String> resolveParamNames(List<LSFExprParamDeclaration> params) {
        if(params == null)
            return null;
        
        List<String> result = new ArrayList<>();
        for(LSFExprParamDeclaration param : params)
            result.add(param.getDeclName());
        return result;
    }

    public static List<LSFParamDeclaration> resolveParams(LSFExprParameterUsageList classNameList) {
        if (classNameList == null)
            return null;

        List<LSFParamDeclaration> result = new ArrayList<>();

        for (LSFExprParameterUsage usage : classNameList.getExprParameterUsageList()) {
            LSFExprParameterNameUsage nameUsage = usage.getExprParameterNameUsage();
            if (nameUsage != null)
                result.add(nameUsage.getClassParamDeclare().getParamDeclare());
        }
        return result;
    }

    @Nullable
    public static List<LSFParamDeclaration> resolveParamDecls(@NotNull LSFPropertyDeclaration propertyDeclaration) {
        List<LSFParamDeclaration> result = null;
        LSFPropertyDeclParams propertyDeclParams = propertyDeclaration.getPropertyDeclParams();
        if (propertyDeclParams != null)
            return resolveParams(propertyDeclParams.getClassParamDeclareList());
        return result;
    }

    // PROPERTYUSAGECONTEXT.RESOLVEPARAMCLASSES

    @NotNull
    public static List<LSFClassSet> resolveParamDeclClasses(Collection<? extends LSFExprParamDeclaration> decls) {
        List<LSFClassSet> result = new ArrayList<>();
        for (LSFExprParamDeclaration decl : decls)
            result.add(decl.resolveClass());
        return result;
    }

    @NotNull
    public static List<LSFClassSet> resolveParamRefClasses(List<? extends LSFAbstractParamReference> refs) {
        List<LSFClassSet> result = new ArrayList<>();
        for (LSFAbstractParamReference ref : refs)
            result.add(ref.resolveClass());
        return result;
    }

    @NotNull
    public static List<LSFExClassSet> resolveExprClasses(List<LSFPropertyExpression> exprs, @Nullable InferExResult inferred) {
        List<LSFExClassSet> result = new ArrayList<>();
        for (LSFPropertyExpression expr : exprs)
            result.add(expr.resolveInferredValueClass(inferred));
        return result;
    }

    @NotNull
    public static List<LSFExClassSet> resolveParamExprClasses(List<LSFExprParameterUsage> exprs) {
        List<LSFExClassSet> result = new ArrayList<>();
        for (LSFExprParameterUsage expr : exprs)
            result.add(resolveInferredValueClass(expr, null));
        return result;
    }

    @NotNull
    public static List<LSFExClassSet> resolveParamOrExprClasses(List<LSFParameterOrExpression> paramOrExprs) {
        List<LSFExClassSet> result = new ArrayList<>();
        for (LSFParameterOrExpression paramOrExpr : paramOrExprs) {
            LSFExClassSet classSet;
            LSFExprParameterUsage param = paramOrExpr.getExprParameterUsage();
            if(param != null)
                classSet = resolveInferredValueClass(param, null);
            else
                classSet = paramOrExpr.getPropertyExpression().resolveInferredValueClass(null); 
            result.add(classSet);
        }
        return result;
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@Nullable LSFClassParamDeclareList sourceStatement) {
        if (sourceStatement == null) {
            return null;
        }
        LSFNonEmptyClassParamDeclareList ne = sourceStatement.getNonEmptyClassParamDeclareList();
        if (ne != null) {
            List<LSFClassSet> result = new ArrayList<>();
            for (LSFClassParamDeclare decl : ne.getClassParamDeclareList())
                result.add(decl.resolveClass());
            return result;
        }

        return new ArrayList<>();
    }

    public static List<LSFObjectUsage> getObjectUsageList(LSFObjectUsageList objectUsageList) {
        if (objectUsageList != null) {
            LSFNonEmptyObjectUsageList neList = objectUsageList.getNonEmptyObjectUsageList();
            if (neList != null)
                return neList.getObjectUsageList();
        }
        return new ArrayList<>();
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@Nullable LSFObjectUsageList sourceStatement) {
        return sourceStatement == null ? null : resolveParamRefClasses(getObjectUsageList(sourceStatement));
    }
    
    public static String getObjectUsageString(@Nullable LSFObjectUsageList sourceStatement) {
        if(sourceStatement == null)
            return "";

        String result = "";
        for(LSFObjectUsage objectUsage : getObjectUsageList(sourceStatement)) {
            result = (result.isEmpty() ? "" : result + ",") + objectUsage.getSimpleName().getName();
        }
        return "(" + result + ")";
    }

    @NotNull
    public static List<LSFExClassSet> resolveParamClasses(@Nullable LSFNonEmptyPropertyExpressionList sourceStatement, @Nullable InferExResult inferred) {
        if(sourceStatement == null)
            return Collections.emptyList();
        return resolveExprClasses(sourceStatement.getPropertyExpressionList(), inferred);
    }

    public static List<LSFExClassSet> resolveParamClasses(@Nullable LSFNonEmptyPropertyExpressionList sourceStatement) {
        if(sourceStatement == null)
            return Collections.emptyList();
        return resolveExprClasses(sourceStatement.getPropertyExpressionList(), null);
    }

    public static List<LSFExClassSet> resolveParamClasses(@Nullable LSFNonEmptyParameterOrExpressionList sourceStatement) {
        if(sourceStatement == null)
            return Collections.emptyList();
        return resolveParamOrExprClasses(sourceStatement.getParameterOrExpressionList());
    }

    public static List<LSFClassSet> resolveParamClasses(@Nullable LSFPropertyExpressionList sourceStatement) {
        if(sourceStatement == null)
            return Collections.emptyList();
        return finishParamClasses(sourceStatement.getNonEmptyPropertyExpressionList());
    }

    public static List<LSFClassSet> resolveParamClasses(@Nullable LSFParameterOrExpressionList sourceStatement) {
        if(sourceStatement == null)
            return Collections.emptyList();
        return finishParamClasses(sourceStatement.getNonEmptyParameterOrExpressionList());
    }

    private static List<LSFClassSet> finishParamClasses(LSFNonEmptyPropertyExpressionList nonEmpty) {
        if (nonEmpty != null)
            return LSFExClassSet.fromEx(resolveParamClasses(nonEmpty));
        return new ArrayList<>();
    }

    private static List<LSFClassSet> finishParamClasses(LSFNonEmptyParameterOrExpressionList nonEmpty) {
        if (nonEmpty != null)
            return LSFExClassSet.fromEx(resolveParamClasses(nonEmpty));
        return new ArrayList<>();
    }

    public static List<LSFExClassSet> resolveParamClasses(@NotNull LSFExprParameterUsageList sourceStatement) {
        return resolveParamExprClasses(sourceStatement.getExprParameterUsageList());
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFJoinPropertyDefinition sourceStatement) {
        return resolveParamClasses(sourceStatement.getPropertyExpressionList());
    }
    
    public static List<LSFPropertyExpression> getList(LSFPropertyExpressionList peList) {
        if(peList == null)
            return Collections.emptyList();
        LSFNonEmptyPropertyExpressionList nepeList = peList.getNonEmptyPropertyExpressionList();
        if(nepeList == null)
            return Collections.emptyList();
        else
            return nepeList.getPropertyExpressionList();
    }

    public static List<LSFParameterOrExpression> getList(LSFParameterOrExpressionList peList) {
        if(peList == null)
            return Collections.emptyList();
        LSFNonEmptyParameterOrExpressionList nepeList = peList.getNonEmptyParameterOrExpressionList();
        if(nepeList == null)
            return Collections.emptyList();
        else
            return nepeList.getParameterOrExpressionList();
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFExecActionPropertyDefinitionBody sourceStatement) {
        LSFPropertyExpressionList peList = sourceStatement.getPropertyExpressionList();
        if (peList == null) {
            return Collections.emptyList();
        }
        return resolveParamClasses(peList);
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFChangePropertyBody sourceStatement) {
        LSFParameterOrExpressionList peList = sourceStatement.getParameterOrExpressionList();
        if (peList == null) {
            return Collections.emptyList();
        }
        return resolveParamClasses(peList);
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFNoContextPropertyUsage sourceStatement) {
        return null;
    }

    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFNoParamsPropertyUsage sourceStatement) {
        return new ArrayList<>();
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFNoContextActionUsage sourceStatement) {
        return null;
    }

    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFNoParamsActionUsage sourceStatement) {
        return new ArrayList<>();
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFNoContextActionOrPropertyUsage sourceStatement) {
        return null;
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFPartitionPropertyDefinition sourceStatement) {
        LSFPartitionPropertyBy by = sourceStatement.getPartitionPropertyBy();
        if (by != null)
            return finishParamClasses(by.getNonEmptyPropertyExpressionList());
        return new ArrayList<>();
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFJoinPropertyDefinition sourceStatement) {
        return sourceStatement.getPropertyExpressionList();
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFPartitionPropertyDefinition sourceStatement) {
        return null;
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFNoContextPropertyUsage sourceStatement) {
        return null;
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFNoParamsPropertyUsage sourceStatement) {
        return null;
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFNoContextActionUsage sourceStatement) {
        return null;
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFNoParamsActionUsage sourceStatement) {
        return null;
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFNoContextActionOrPropertyUsage sourceStatement) {
        return null;
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFExecActionPropertyDefinitionBody sourceStatement) {
        return sourceStatement.getPropertyExpressionList();
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFChangePropertyBody sourceStatement) {
        return sourceStatement.getParameterOrExpressionList();
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFFormTreeGroupObjectDeclaration sourceStatement) {
        return null;
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFFormMappedNamePropertiesList sourceStatement) {
        return null;
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFFormPropertyObject sourceStatement) {
        return sourceStatement.getObjectUsageList();
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFFormActionObject sourceStatement) {
        return sourceStatement.getObjectUsageList();
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFFormPropertyDrawObject sourceStatement) {
        return sourceStatement.getObjectUsageList();
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFMappedPropertyClassParamDeclare sourceStatement) {
        return sourceStatement.getClassParamDeclareList();
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFMappedActionClassParamDeclare sourceStatement) {
        return sourceStatement.getClassParamDeclareList();
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFMappedPropertyExprParam sourceStatement) {
        return sourceStatement.getExprParameterUsageList();
    }

    private static List<LSFFormObjectDeclaration> getObjectDecls(LSFFormGroupObject commonGroup) {
        LSFFormSingleGroupObjectDeclaration singleGroup = commonGroup.getFormSingleGroupObjectDeclaration();
        if (singleGroup != null)
            return singletonList(singleGroup.getFormObjectDeclaration());

        LSFFormMultiGroupObjectDeclaration formMultiGroupObjectDeclaration = commonGroup.getFormMultiGroupObjectDeclaration();

        assert formMultiGroupObjectDeclaration != null;

        return formMultiGroupObjectDeclaration.getFormObjectDeclarationList();
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFFormTreeGroupObjectDeclaration sourceStatement) {
        return resolveParamDeclClasses(getObjectDecls(sourceStatement.getFormGroupObject()));
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFFormMappedNamePropertiesList sourceStatement) {
        return resolveParamClasses(sourceStatement.getObjectUsageList());
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFFormPropertyObject sourceStatement) {
        return resolveParamClasses(sourceStatement.getObjectUsageList());
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFFormActionObject sourceStatement) {
        return resolveParamClasses(sourceStatement.getObjectUsageList());
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFFormPropertyDrawObject sourceStatement) {
        return resolveParamClasses(sourceStatement.getObjectUsageList());
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFMappedPropertyClassParamDeclare sourceStatement) {
        return resolveParamClasses(sourceStatement.getClassParamDeclareList());
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFMappedActionClassParamDeclare sourceStatement) {
        return resolveParamClasses(sourceStatement.getClassParamDeclareList());
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFMappedPropertyExprParam sourceStatement) {
        LSFExprParameterUsageList exprParameterUsageList = sourceStatement.getExprParameterUsageList();
        return exprParameterUsageList == null ? null : LSFExClassSet.fromEx(resolveParamClasses(exprParameterUsageList));
    }

    public static List<LSFClassSet> resolveParamClasses(LSFHeadersPropertyUsage sourceStatement) {
        return Collections.singletonList(new StringClass(false, false, ExtInt.UNLIMITED));
    }

    public static List<LSFClassSet> resolveParamClasses(LSFImportPropertyUsage sourceStatement) {

        LSFImportActionPropertyDefinitionBody importDB = PsiTreeUtil.getParentOfType(sourceStatement, LSFImportActionPropertyDefinitionBody.class);
        if (importDB != null) {
            LSFClassNameList classNameList = importDB.getClassNameList();
            if(classNameList != null) {
                LSFNonEmptyClassNameList nonEmptyClassNameList = classNameList.getNonEmptyClassNameList();
                if(nonEmptyClassNameList == null) {
                    return Collections.emptyList();
                }
            }
        }
        return Collections.singletonList(IntegerClass.instance);
    }

    public static List<LSFClassSet> resolveParamClasses(LSFEmailPropertyUsage sourceStatement) {
        return Collections.singletonList(IntegerClass.instance);
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFHeadersPropertyUsage sourceStatement) {
        return null;
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFImportPropertyUsage sourceStatement) {
        return null;
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFEmailPropertyUsage sourceStatement) {
        return null;
    }
    
    // PROPERTYEXPRESSION.INFERPARAMCLASSES

    @NotNull
    public static Inferred inferExpressionParamClasses(@Nullable LSFExpression sourceStatement, @Nullable LSFExClassSet valueClass) {
        return sourceStatement == null ? Inferred.EMPTY : sourceStatement.inferParamClasses(valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFPropertyExpression sourceStatement, @Nullable LSFExClassSet valueClass) {
        return inferParamClasses(sourceStatement.getIfPE(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFAliasedPropertyExpression sourceStatement, @Nullable LSFExClassSet valueClass) {
        return inferParamClasses(sourceStatement.getPropertyExpression(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFIfPE sourceStatement, @Nullable LSFExClassSet valueClass) {
        List<LSFOrPE> peList = sourceStatement.getOrPEList();

        Inferred result = inferParamClasses(peList.get(0), valueClass);
        for (int i = 1; i < peList.size(); i++)
            result = result.and(inferParamClasses(peList.get(i), null)); // подойдут все классы, так как boolean
        return result;
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFOrPE sourceStatement, @Nullable LSFExClassSet valueClass) {
        List<LSFXorPE> peList = sourceStatement.getXorPEList();
        if (peList.size() == 1)
            return inferParamClasses(peList.get(0), valueClass);

        List<Inferred> maps = new ArrayList<>();
        for (LSFXorPE xorPe : peList)
            maps.add(inferParamClasses(xorPe, null)); // подойдут все классы, так как boolean
        return Inferred.orClasses(maps);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFXorPE sourceStatement, @Nullable LSFExClassSet valueClass) {
        List<LSFAndPE> peList = sourceStatement.getAndPEList();
        if (peList.size() == 1)
            return inferParamClasses(peList.get(0), valueClass);

        List<Inferred> maps = new ArrayList<>();
        for (LSFAndPE andPe : peList)
            maps.add(inferParamClasses(andPe, null)); // подойдут все классы, так как boolean
        return Inferred.orClasses(maps);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFAndPE sourceStatement, @Nullable LSFExClassSet valueClass) {
        List<LSFNotPE> peList = sourceStatement.getNotPEList();
        if (peList.size() == 1)
            return inferParamClasses(peList.get(0), valueClass);

        List<Inferred> maps = new ArrayList<>();
        for (LSFNotPE notPE : peList)
            maps.add(inferParamClasses(notPE, null)); // подойдут все классы, так как boolean
        return Inferred.andClasses(maps);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFNotPE sourceStatement, @Nullable LSFExClassSet valueClass) {
        LSFEqualityPE eqPE = sourceStatement.getEqualityPE();
        if (eqPE != null)
            return inferParamClasses(eqPE, valueClass);

        LSFNotPE notPE = sourceStatement.getNotPE();

        assert notPE != null;

        return new Inferred(inferParamClasses(notPE, null), true);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFEqualityPE sourceStatement, @Nullable LSFExClassSet valueClass) {
        List<LSFRelationalPE> list = sourceStatement.getRelationalPEList();
        if (list.size() == 2)
            return Inferred.create(new Equals(list.get(0), list.get(1)));
        return inferParamClasses(list.get(0), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFRelationalPE sourceStatement, @Nullable LSFExClassSet valueClass) {
        List<LSFLikePE> list = sourceStatement.getLikePEList();

        if (list.size() == 2)
            return Inferred.create(new Relationed(list.get(0), list.get(1)));
        return inferParamClasses(list.get(0), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFLikePE sourceStatement, @Nullable LSFExClassSet valueClass) {
        List<Inferred> maps = new ArrayList<>();
        for (LSFAdditiveORPE additiveORPE : sourceStatement.getAdditiveORPEList())
            maps.add(inferParamClasses(additiveORPE, valueClass));
        return Inferred.orClasses(maps);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFAdditiveORPE sourceStatement, @Nullable LSFExClassSet valueClass) {
        List<Inferred> maps = new ArrayList<>();
        for (LSFAdditivePE additivePE : sourceStatement.getAdditivePEList())
            maps.add(inferParamClasses(additivePE, valueClass));
        return Inferred.orClasses(maps);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFAdditivePE sourceStatement, @Nullable LSFExClassSet valueClass) {
        List<Inferred> maps = new ArrayList<>();
        for (LSFMultiplicativePE additivePE : sourceStatement.getMultiplicativePEList())
            maps.add(inferParamClasses(additivePE, valueClass));
        return Inferred.andClasses(maps);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFMultiplicativePE sourceStatement, @Nullable LSFExClassSet valueClass) {
        List<Inferred> maps = new ArrayList<>();
        for (LSFUnaryMinusPE additivePE : sourceStatement.getUnaryMinusPEList())
            maps.add(inferParamClasses(additivePE, valueClass));
        return Inferred.andClasses(maps);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFUnaryMinusPE sourceStatement, @Nullable LSFExClassSet valueClass) {
        LSFUnaryMinusPE unaryMinusPE = sourceStatement.getUnaryMinusPE();
        if (unaryMinusPE != null)
            return inferParamClasses(unaryMinusPE, valueClass);

        LSFPostfixUnaryPE postfixUnaryPE = sourceStatement.getPostfixUnaryPE();

        assert postfixUnaryPE != null;

        return inferParamClasses(postfixUnaryPE, valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFPostfixUnaryPE sourceStatement, @Nullable LSFExClassSet valueClass) {
        LSFUintLiteral uintLiteral = sourceStatement.getUintLiteral();
        if (uintLiteral != null) {
            if (valueClass != null && valueClass.classSet instanceof ConcatenateClassSet)
                valueClass = LSFExClassSet.checkNull(((ConcatenateClassSet) valueClass.classSet).getSet(Integer.parseInt(uintLiteral.getText()) - 1), valueClass.orAny);
            else
                valueClass = null;
        }

        LSFTypePropertyDefinition typeDef = sourceStatement.getTypePropertyDefinition();
        if (typeDef != null)
            return inferParamClasses(sourceStatement.getSimplePE(), LSFExClassSet.toEx(resolveClass(typeDef.getClassName())));

        return inferParamClasses(sourceStatement.getSimplePE(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFSimplePE sourceStatement, @Nullable LSFExClassSet valueClass) {
        LSFPropertyExpression pe = sourceStatement.getPropertyExpression();
        if (pe != null)
            return inferParamClasses(pe, valueClass);

        LSFExpressionPrimitive expressionPrimitive = sourceStatement.getExpressionPrimitive();

        assert expressionPrimitive != null;

        return inferParamClasses(expressionPrimitive, valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFExpressionPrimitive sourceStatement, @Nullable LSFExClassSet valueClass) {
        LSFExprParameterUsage ep = sourceStatement.getExprParameterUsage();
        if (ep != null)
            return inferParamClasses(ep, valueClass);

        LSFExpressionFriendlyPD expressionFriendlyPD = sourceStatement.getExpressionFriendlyPD();

        assert expressionFriendlyPD != null;

        return inferParamClasses(expressionFriendlyPD, valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFExprParameterUsage sourceStatement, @Nullable LSFExClassSet valueClass) {
        LSFExprParameterNameUsage nameParam = sourceStatement.getExprParameterNameUsage();
        if (nameParam != null) {
            LSFExprParamDeclaration decl = nameParam.resolveDecl();

            if (decl != null) {
                LSFExClassSet declClass = LSFExClassSet.toEx(decl.resolveClass());
                if (declClass == null)
                    declClass = valueClass;
                return new Inferred(decl, declClass);
            }
        }
        return Inferred.EMPTY;
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFExpressionFriendlyPD sourceStatement, @Nullable LSFExClassSet valueClass) {
        return ((LSFExpression) sourceStatement.getChildren()[0]).inferParamClasses(valueClass);
    }

    public static Inferred inferJoinParamClasses(LSFPropertyUsage usage, LSFPropertyExprObject exprObject, LSFPropertyExpressionList peList, @Nullable LSFExClassSet valueClass) {
        List<LSFExClassSet> joinClasses = inferParamClasses(valueClass, usage, exprObject);
        return inferParamClasses(peList, joinClasses);
    }

    public static Inferred inferJoinParamClasses(LSFPropertyUsage usage, LSFParameterOrExpressionList peList, @Nullable LSFExClassSet valueClass) {
        List<LSFExClassSet> joinClasses = inferParamClasses(valueClass, usage, null);
        return inferParamClasses(peList, joinClasses);
    }
    
    public static Inferred inferExecParamClasses(@NotNull LSFActionUsage actionUsage, LSFPropertyExpressionList peList) {
        List<LSFExClassSet> joinClasses = inferParamClasses(null, actionUsage);
        return inferParamClasses(peList, joinClasses);
    }

    public static Inferred inferParamClasses(LSFPropertyExpressionList peList, List<LSFExClassSet> joinClasses) {
        List<LSFPropertyExpression> list = getList(peList);

        List<Inferred> classes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
            classes.add(inferParamClasses(list.get(i), joinClasses != null && i < joinClasses.size() ? joinClasses.get(i) : null));
        return Inferred.andClasses(classes);
    }

    public static Inferred inferParamClasses(LSFParameterOrExpressionList peList, List<LSFExClassSet> joinClasses) {
        List<LSFParameterOrExpression> list = getList(peList);

        List<Inferred> classes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            LSFParameterOrExpression paramOrExpr = list.get(i);
            LSFExClassSet classSet = joinClasses != null && i < joinClasses.size() ? joinClasses.get(i) : null;
            LSFExprParameterUsage param = paramOrExpr.getExprParameterUsage();
            if(param != null)
                classes.add(inferParamClasses(param, classSet));
            else
                classes.add(inferParamClasses(paramOrExpr.getPropertyExpression(), classSet));
        }
        return Inferred.andClasses(classes);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFJoinPropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        return inferJoinParamClasses(sourceStatement.getPropertyUsage(), sourceStatement.getPropertyExprObject(), sourceStatement.getPropertyExpressionList(), valueClass);
    }

    @Nullable
    private static List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass, LSFActionOrPropReference<?, ?> usage) {
        LSFActionOrPropDeclaration decl = usage.resolveDecl();
        if (decl != null)
            return decl.inferParamClasses(valueClass);
        return null;
    }

    private static List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass, LSFPropertyUsage usage, LSFPropertyExprObject exprObject) {
        List<LSFExClassSet> joinClasses;
        if (usage != null) {
            joinClasses = inferParamClasses(valueClass, usage);
        } else {
            assert exprObject != null;

            LSFPropertyCalcStatement pCalcStatement = exprObject.getPropertyCalcStatement();
            if(pCalcStatement == null)
                return Collections.emptyList();
            LSFPropertyExpression pe = pCalcStatement.getPropertyExpression();
            if (pe != null) {
                List<LSFExprParamDeclaration> params = pe.resolveParams();
                joinClasses = new ArrayList<>();
                InferExResult inferred = inferParamClasses(pe, valueClass).finishEx();
                for (LSFExprParamDeclaration param : params)
                    joinClasses.add(inferred.get(param));
            } else {
                LSFExpressionUnfriendlyPD unfriendlyPD = pCalcStatement.getExpressionUnfriendlyPD();

                assert unfriendlyPD != null;

                PsiElement element = unfriendlyPD.getChildren()[0]; // лень создавать отдельный параметр или интерфейс
                if (element instanceof LSFGroupPropertyDefinition) {
                    joinClasses = inferGroupValueParamClasses((LSFGroupPropertyDefinition) element);
                } else
                    joinClasses = unfriendlyPD.resolveValueParamClasses(null);
            }
//                else {
//                LSFListActionPropertyDefinitionBody body = exprObject.getListActionPropertyDefinitionBody();
//                if(body != null) {
//                    joinClasses = null;
//                } else { // contextUnfriendly
//                    joinClasses = LSFPsiImplUtil.resolveValueParamClasses(exprObject.getActionUnfriendlyPD());
//                }
//
//            }
        }
        return joinClasses;
    }

    @NotNull
    private static Inferred inferParamClasses(List<LSFPropertyExpression> list, @Nullable LSFExClassSet valueClass) {
        if (list.size() == 0)
            return Inferred.EMPTY;

        List<Inferred> classes = new ArrayList<>();
        for (LSFPropertyExpression expr : list)
            classes.add(inferParamClasses(expr, valueClass));
        return Inferred.orClasses(classes);
    }

    private static Inferred inferParamClasses(@Nullable LSFNonEmptyPropertyExpressionList list, @Nullable LSFExClassSet valueClass) {
        if (list == null) {
            return Inferred.EMPTY;
        }
        return inferParamClasses(list.getPropertyExpressionList(), valueClass);
    }

    @NotNull
    private static Inferred inferAliasedParamClasses(List<LSFAliasedPropertyExpression> list, @Nullable LSFExClassSet valueClass) {
        if (list.size() == 0)
            return Inferred.EMPTY;

        List<Inferred> classes = new ArrayList<>();
        for (LSFAliasedPropertyExpression expr : list)
            classes.add(inferParamClasses(expr, valueClass));
        return Inferred.orClasses(classes);
    }

    private static Inferred inferParamClasses(@Nullable LSFNonEmptyAliasedPropertyExpressionList list, @Nullable LSFExClassSet valueClass) {
        if (list == null) {
            return Inferred.EMPTY;
        }
        return inferAliasedParamClasses(list.getAliasedPropertyExpressionList(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFMultiPropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        return inferParamClasses(sourceStatement.getNonEmptyPropertyExpressionList(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFOverridePropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        return inferParamClasses(sourceStatement.getNonEmptyPropertyExpressionList(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFIfElsePropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        List<LSFPropertyExpression> list = sourceStatement.getPropertyExpressionList();
        if (list.isEmpty()) {
            return Inferred.EMPTY;
        }

        Inferred result = inferParamClasses(list.get(0), null);
        if (list.size() > 1) {
            result = result.and(inferParamClasses(list.get(1), valueClass));
        }
        if (list.size() > 2)
            result = result.or(inferParamClasses(list.get(2), valueClass));
        return result;
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFMaxPropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        return inferParamClasses(sourceStatement.getNonEmptyPropertyExpressionList(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFCasePropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        List<Inferred> list = new ArrayList<>();
        for (LSFCaseBranchBody caseBranch : sourceStatement.getCaseBranchBodyList()) {
            List<LSFPropertyExpression> peList = caseBranch.getPropertyExpressionList();

            Inferred branchInferred = Inferred.EMPTY;
            if (peList.size() > 0) {
                branchInferred = inferParamClasses(peList.get(0), null);
                if (peList.size() > 1) {
                    branchInferred = branchInferred.and(inferParamClasses(peList.get(1), valueClass));
                }
            }

            list.add(branchInferred);
        }
        LSFPropertyExpression elseExpr = sourceStatement.getPropertyExpression();
        if (elseExpr != null)
            list.add(inferParamClasses(elseExpr, valueClass));
        return Inferred.orClasses(list);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFPartitionPropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        List<Inferred> classes = new ArrayList<>();
        classes.add(inferExpressionParamClasses(sourceStatement.getPropertyExpression(), valueClass));

        LSFPartitionPropertyBy partitionBy = sourceStatement.getPartitionPropertyBy();
        if (partitionBy != null) {
            List<LSFExClassSet> joinClasses = null;
            LSFPropertyUsage pu = sourceStatement.getPropertyUsage();
            if (pu != null)
                joinClasses = inferParamClasses(valueClass, pu);

            List<LSFPropertyExpression> list = partitionBy.getNonEmptyPropertyExpressionList().getPropertyExpressionList();
            for (int i = 0; i < list.size(); i++)
                classes.add(inferParamClasses(list.get(i), joinClasses != null && i < joinClasses.size() ? joinClasses.get(i) : null));
        }

        LSFNonEmptyPropertyExpressionList peList = sourceStatement.getNonEmptyPropertyExpressionList();
        if (peList != null)
            for (LSFPropertyExpression pe : peList.getPropertyExpressionList())
                classes.add(inferParamClasses(pe, null));

        return Inferred.andClasses(classes);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFRecursivePropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        List<LSFPropertyExpression> peList = sourceStatement.getPropertyExpressionList();
        if (peList.isEmpty()) {
            return Inferred.EMPTY;
        }

        Inferred inferred = inferParamClasses(peList.get(0), valueClass);
        if (peList.size() == 1) {
            return inferred;
        }

        return inferred.or(inferParamClasses(peList.get(1), valueClass));
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFGroupExprPropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        LSFGroupPropertyBody body = sourceStatement.getGroupPropertyBody();
        if(body != null) {
            List<Inferred> classes = new ArrayList<>();
            classes.add(inferParamClasses(body.getNonEmptyPropertyExpressionList(), valueClass));
            LSFOrderPropertyBy order = body.getOrderPropertyBy();
            if(order != null)
                classes.add(inferParamClasses(order.getNonEmptyPropertyExpressionList(), null));
            LSFPropertyExpression where = body.getPropertyExpression();
            if(where != null)
                classes.add(inferParamClasses(where, null));
            return Inferred.andClasses(classes);
        }
        return Inferred.EMPTY;
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFStructCreationPropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        List<Inferred> classes = new ArrayList<>();
        LSFNonEmptyPropertyExpressionList nonEmptyPropertyExpressionList = sourceStatement.getNonEmptyPropertyExpressionList();
        if (nonEmptyPropertyExpressionList == null) {
            return Inferred.EMPTY;
        }

        for (LSFPropertyExpression pe : nonEmptyPropertyExpressionList.getPropertyExpressionList())
            classes.add(inferParamClasses(pe, null));
        return Inferred.andClasses(classes);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFCastPropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        return inferParamClasses(sourceStatement.getPropertyExpression(), null); // из-за cast'а
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFConcatPropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        return inferParamClasses(sourceStatement.getNonEmptyPropertyExpressionList(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFJsonPropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        return inferParamClasses(sourceStatement.getNonEmptyAliasedPropertyExpressionList(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFJsonFormPropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        return Inferred.EMPTY;
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFSessionPropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        return inferParamClasses(sourceStatement.getPropertyExpression(), null);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFSignaturePropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        return inferParamClasses(sourceStatement.getPropertyExpression(), null);
    }

    public
    @NotNull static Inferred inferParamClasses(@NotNull LSFActiveTabPropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        return Inferred.EMPTY;
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFRoundPropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        return Inferred.EMPTY;
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFExpressionLiteral sourceStatement, @Nullable LSFExClassSet valueClass) {
        return Inferred.EMPTY;
    }

    public static void ensureClass(@NotNull LSFClassParamDeclare sourceStatement, @NotNull LSFValueClass valueClass, MetaTransaction metaTrans) {
        LSFClassName className = sourceStatement.getClassName();
        if (className == null) {
            LeafElement whitespace = ASTFactory.whitespace(" ");
            LSFClassName genName = LSFElementGenerator.createClassNameFromText(sourceStatement.getProject(), valueClass.getQName(sourceStatement));
            ASTNode paramDeclare = sourceStatement.getParamDeclare().getNode();

            if (metaTrans != null) // !! важно до делать
                metaTrans.regChange(BaseUtils.toList(genName.getNode(), whitespace), paramDeclare, MetaTransaction.Type.BEFORE);
            
            ASTNode sourceNode = sourceStatement.getNode();
            sourceNode.addChild(whitespace, paramDeclare);
            sourceNode.addChild(genName.getNode(), whitespace);
        }
    }

    /////
    public static void ensureClass(@NotNull LSFForAddObjClause sourceStatement, @NotNull LSFValueClass valueClass, MetaTransaction metaTrans) {
    }

    public static void ensureClass(@NotNull LSFFormActionObjectUsage sourceStatement, @NotNull LSFValueClass valueClass, MetaTransaction metaTrans) {
    }

    public static void ensureClass(@NotNull LSFImportFieldDefinition sourceStatement, @NotNull LSFValueClass valueClass, MetaTransaction metaTrans) {
    }

    public static void ensureClass(@NotNull LSFInputActionPropertyDefinitionBody sourceStatement, @NotNull LSFValueClass valueClass, MetaTransaction metaTrans) {
    }

    public static void ensureClass(@NotNull LSFConfirmActionPropertyDefinitionBody sourceStatement, @NotNull LSFValueClass valueClass, MetaTransaction metaTrans) {
    }

    public static Inferred inferActionParamClasses(@Nullable LSFActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return body == null ? Inferred.EMPTY : ((ActionExpression) body.getChildren()[0]).inferActionParamClasses(params);
    }

    public static Inferred inferActionParamClasses(LSFAssignActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        List<LSFPropertyExpression> peList = body.getPropertyExpressionList();
        if (peList.isEmpty()) {
            return Inferred.EMPTY;
        }
        LSFPropertyExpression peValue = peList.get(0);
        LSFChangePropertyBody peTo = body.getChangePropertyBody();

        assert peTo != null; // т.к. !peList.isEmpty()

        Inferred result = inferJoinParamClasses(peTo.getPropertyUsage(), peTo.getParameterOrExpressionList(), peValue.resolveValueClass(true)).filter(params);
        //orClasses(BaseUtils.filterNullable(inferParamClasses(peValue, resolveValueClass(peTo.getPropertyUsage(), true)), params)
        if (peList.size() == 2)
            result = result.and(inferParamClasses(peList.get(1), null).filter(params));
        return inferParamClasses(peValue, resolveValueClass(peTo.getPropertyUsage(), true)).override(result);
    }

    public static Inferred inferActionParamClasses(LSFForActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а 
        LSFPropertyExpression expr = body.getForActionPropertyMainBody().getPropertyExpression();
        Inferred forClasses = Inferred.EMPTY;
        if (expr != null)
            forClasses = forClasses.and(inferParamClasses(expr, null).filter(params));
        LSFNonEmptyPropertyExpressionList npeList = body.getForActionPropertyMainBody().getNonEmptyPropertyExpressionList();
        if (npeList != null) {
            for (LSFPropertyExpression pe : npeList.getPropertyExpressionList())
                forClasses = forClasses.and(inferParamClasses(pe, null).filter(params));
        }

        Inferred result = Inferred.EMPTY;
        LSFActionPropertyDefinitionBody doAction = body.getForActionPropertyMainBody().getActionPropertyDefinitionBody();
        if (doAction != null) {
            result = inferActionParamClasses(doAction, params).override(forClasses);
        }
        LSFActionPropertyDefinitionBody elseAction = body.getActionPropertyDefinitionBody();
        if (elseAction != null) {
            result = result.or(inferActionParamClasses(elseAction, params));    
        }
        return result;
    }

    public static Inferred inferActionParamClasses(LSFExportDataActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а 
        LSFWherePropertyExpression expr = body.getWherePropertyExpression();
        Inferred result = Inferred.EMPTY;
        if (expr != null)
            result = result.and(inferParamClasses(expr.getPropertyExpression(), null).filter(params));
        LSFNonEmptyAliasedPropertyExpressionList npeList = body.getNonEmptyAliasedPropertyExpressionList();
        if (npeList != null) {
            for (LSFAliasedPropertyExpression pe : npeList.getAliasedPropertyExpressionList())
                result = result.and(inferParamClasses(pe.getPropertyExpression(), null).filter(params));
        }
        return result;
    }

    public static Inferred inferActionParamClasses(LSFNewActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        Inferred result = Inferred.EMPTY;
        LSFActionPropertyDefinitionBody doAction = body.getActionPropertyDefinitionBody();
        if (doAction != null) {
            result = inferActionParamClasses(doAction, params);
        }
        return result;
    }

    public static Inferred inferActionParamClasses(LSFRecalculateActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFWhileActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а

        Inferred forClasses = inferExpressionParamClasses(body.getPropertyExpression(), null).filter(params);

        LSFNonEmptyPropertyExpressionList npeList = body.getNonEmptyPropertyExpressionList();
        if (npeList != null) {
            for (LSFPropertyExpression pe : npeList.getPropertyExpressionList())
                forClasses = forClasses.and(inferParamClasses(pe, null).filter(params));
        }

        return inferActionParamClasses(body.getActionPropertyDefinitionBody(), params).override(forClasses);
    }

    public static Inferred inferActionParamClasses(@Nullable LSFChangeClassWhere where) {
        if(where != null)
            return inferExpressionParamClasses(where.getPropertyExpression(), null);
        return Inferred.EMPTY;        
    }
    public static Inferred inferActionParamClasses(LSFChangeClassActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а
        return inferActionParamClasses(body.getChangeClassWhere()).filter(params);
    }

    public static Inferred inferActionParamClasses(LSFDeleteActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return inferActionParamClasses(body.getChangeClassWhere()).filter(params);
    }

    public static Inferred inferParamClasses(LSFMappedPropertyExprParam mappedProperty, @Nullable LSFExClassSet valueClass) {
        List<LSFExClassSet> joinClasses = inferParamClasses(null, mappedProperty.getPropertyUsage());

        LSFExprParameterUsageList exprParameterUsageList = mappedProperty.getExprParameterUsageList();
        if (exprParameterUsageList == null) {
            return Inferred.EMPTY;
        }

        List<LSFExprParameterUsage> list = exprParameterUsageList.getExprParameterUsageList();
        List<Inferred> classes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
            classes.add(inferParamClasses(list.get(i), joinClasses != null && i < joinClasses.size() ? joinClasses.get(i) : null));
        return Inferred.andClasses(classes);
    }

    public static Inferred inferActionParamClasses(LSFNewWhereActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а
        Inferred result = Inferred.EMPTY;

        LSFPropertyExpression expr = body.getPropertyExpression();
        if (expr != null)
            result = inferParamClasses(expr, null).filter(params);

        LSFMappedPropertyExprParam to = body.getMappedPropertyExprParam();
        if (to != null)
            result = inferParamClasses(to, LSFExClassSet.toEx(resolveClass(body.getCustomClassUsage()))).filter(params).override(result);
        return result;
    }

    public static Inferred inferActionParamClasses(LSFListActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а
        Inferred result = Inferred.EMPTY;
        for (LSFActionPropertyDefinitionBody action : body.getActionPropertyDefinitionBodyList())
            result = result.or(inferActionParamClasses(action, params));
        return result;
    }

    public static Inferred inferActionParamClasses(LSFRequestActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        List<LSFActionPropertyDefinitionBody> actions = body.getActionPropertyDefinitionBodyList();
        if (actions.isEmpty()) {
            return Inferred.EMPTY;
        }

        Inferred result = inferActionParamClasses(actions.get(0), params);
        if (actions.size() == 2)
            result = result.or(inferActionParamClasses(actions.get(1), params));
        return result;
    }

    public static Inferred inferActionParamClasses(LSFInputActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        Inferred inputInferred = new ExprsContextInferrer(body.getPropertyExpression()).inferClasses(params);
        return inferDoInputBody(body.getDoInputBody(), inputInferred, params);
    }

    public static Inferred inferActionParamClasses(LSFActiveFormActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFActivateActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFCloseFormActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFExpandCollapseActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFExecActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        LSFActionUsage actionUsage = body.getActionUsage();
        if (actionUsage == null) {
            return Inferred.EMPTY;
        }
        return inferExecParamClasses(actionUsage, body.getPropertyExpressionList()).filter(params);
    }

    public static Inferred inferActionParamClasses(LSFIfActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а
        Inferred forClasses = inferExpressionParamClasses(body.getPropertyExpression(), null).filter(params);

        List<LSFActionPropertyDefinitionBody> actions = body.getActionPropertyDefinitionBodyList();
        if (actions.isEmpty()) {
            return forClasses;
        }
        Inferred result = inferActionParamClasses(actions.get(0), params).override(forClasses);
        if (actions.size() == 2)
            result = result.or(inferActionParamClasses(actions.get(1), params));
        return result;
    }

    public static Inferred inferActionParamClasses(LSFCaseActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а 
        List<Inferred> list = new ArrayList<>();
        for (LSFActionCaseBranchBody caseBranch : body.getActionCaseBranchBodyList()) {
            LSFPropertyExpression whenExpression = caseBranch.getPropertyExpression();
            Inferred inferred = Inferred.EMPTY;
            if (whenExpression != null) {
                inferred = inferParamClasses(whenExpression, null).filter(params);
                LSFActionPropertyDefinitionBody thenActionBody = caseBranch.getActionPropertyDefinitionBody();
                if (thenActionBody != null) {
                    inferred = inferActionParamClasses(thenActionBody, params).override(inferred);
                }
            }
            list.add(inferred);
        }

        LSFActionPropertyDefinitionBody elseAction = body.getActionPropertyDefinitionBody();
        if (elseAction != null)
            list.add(inferActionParamClasses(elseAction, params));
        return Inferred.orClasses(list);
    }

    public static Inferred inferActionParamClasses(LSFMultiActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        LSFNonEmptyActionPDBList nonEmptyActionPDBList = body.getNonEmptyActionPDBList();
        if (nonEmptyActionPDBList == null) {
            return Inferred.EMPTY;
        }

        List<Inferred> list = new ArrayList<>();
        for (LSFActionPropertyDefinitionBody action : nonEmptyActionPDBList.getActionPropertyDefinitionBodyList()) {
            list.add(inferActionParamClasses(action, params));
        }
        return Inferred.orClasses(list);
    }

    public static Inferred inferActionParamClasses(LSFTerminalFlowActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFFormActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        List<Inferred> list = new ArrayList<>();

        List<LSFFormActionObjectUsage> objectMap = new ArrayList<>();
        LSFFormActionObjectList objects = body.getFormActionObjectList();
        if (objects != null)
            objectMap.addAll(objects.getFormActionObjectUsageList());

        for (LSFFormActionObjectUsage ou : objectMap) {
            LSFObjectInProps objectInProps = ou.getObjectInProps();
            if(objectInProps != null) {
                list.add(
                        inferExpressionParamClasses(objectInProps.getPropertyExpression(), LSFExClassSet.toEx(ou.getObjectUsage().resolveClass())).filter(params)
                );
            }
        }
        return Inferred.andClasses(list); // по идее and так как на сервере and
    }

    public static Inferred inferActionParamClasses(LSFDialogActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        List<Inferred> list = new ArrayList<>();

        List<LSFFormActionObjectUsage> objectMap = new ArrayList<>();
        LSFFormActionObjectList objects = body.getFormActionObjectList();
        if (objects != null)
            objectMap.addAll(objects.getFormActionObjectUsageList());

        for (LSFFormActionObjectUsage ou : objectMap) {
            LSFObjectInProps objectInProps = ou.getObjectInProps();
            if(objectInProps != null) {
                list.add(
                        inferExpressionParamClasses(objectInProps.getPropertyExpression(), LSFExClassSet.toEx(ou.getObjectUsage().resolveClass())).filter(params)
                );
            }
        }
        Inferred forClasses = Inferred.andClasses(list); // по идее and так как на сервере and

        return inferDoInputBody(body.getDoInputBody(), forClasses, params);
    }

    public static Inferred inferDoInputBody(LSFDoInputBody body, Inferred inferred, @Nullable Set<LSFExprParamDeclaration> params) {
        if (body != null) {
            LSFDoMainBody doMainBody = body.getDoMainBody();
            if (doMainBody != null) {
                LSFActionPropertyDefinitionBody action = doMainBody.getActionPropertyDefinitionBody();
                inferred = inferActionParamClasses(action, params).override(inferred);
            }
        }
        return inferred;
    }

    public static Inferred inferActionParamClasses(LSFPrintActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        List<Inferred> list = new ArrayList<>();

        List<LSFFormActionObjectUsage> objectMap = new ArrayList<>();
        LSFFormActionObjectList objects = body.getFormActionObjectList();
        if (objects != null)
            objectMap.addAll(objects.getFormActionObjectUsageList());

        for (LSFFormActionObjectUsage ou : objectMap) {
            LSFObjectInProps objectInProps = ou.getObjectInProps();
            if(objectInProps != null) {
                list.add(
                        inferExpressionParamClasses(objectInProps.getPropertyExpression(), LSFExClassSet.toEx(ou.getObjectUsage().resolveClass())).filter(params)
                );
            }
        }
        return Inferred.andClasses(list); // по идее and так как на сервере and
    }

    public static Inferred inferActionParamClasses(LSFExportActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        List<Inferred> list = new ArrayList<>();

        List<LSFFormActionObjectUsage> objectMap = new ArrayList<>();
        LSFFormActionObjectList objects = body.getFormActionObjectList();
        if (objects != null)
            objectMap.addAll(objects.getFormActionObjectUsageList());

        for (LSFFormActionObjectUsage ou : objectMap) {
            LSFObjectInProps objectInProps = ou.getObjectInProps();
            if (objectInProps != null) {
                list.add(
                        inferExpressionParamClasses(objectInProps.getPropertyExpression(), LSFExClassSet.toEx(ou.getObjectUsage().resolveClass())).filter(params)
                );
            }
        }
        return Inferred.andClasses(list); // по идее and так как на сервере and
    }

    public static Inferred inferActionParamClasses(LSFMessageActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return inferExpressionParamClasses(body.getPropertyExpressionList().get(0), null).filter(params);
    }

    public static Inferred inferActionParamClasses(LSFConfirmActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        Inferred inputInferred = inferExpressionParamClasses(body.getPropertyExpressionList().get(0), null).filter(params);
        return inferDoInputBody(body.getDoInputBody(), inputInferred, params);
    }

    public static Inferred inferActionParamClasses(LSFAsyncUpdateActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return inferExpressionParamClasses(body.getPropertyExpression(), null).filter(params);
    }

    public static Inferred inferActionParamClasses(LSFSeekObjectActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        LSFObjectID objectID = body.getObjectID();
        LSFClassSet valueClass = null;
        if (objectID != null) {
            LSFObjectUsage objectUsage = objectID.getObjectUsage();
            if (objectUsage != null) {
                valueClass = objectUsage.resolveClass();
            }
        }
        return inferExpressionParamClasses(body.getPropertyExpression(), LSFExClassSet.toEx(valueClass)).filter(params);
    }

    public static Inferred inferActionParamClasses(LSFExpandGroupObjectActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFCollapseGroupObjectActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFOrderActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFReadOrderActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFFilterActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFReadFilterActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFFilterGroupActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFReadFilterGroupsActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFFilterPropertyActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFReadFiltersPropertyActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFEmailActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        List<Inferred> list = new ArrayList<>();
        for (LSFPropertyExpression pe : body.getPropertyExpressionList()) {
            list.add(inferParamClasses(pe, null).filter(params));
        }

        return Inferred.orClasses(list);
    }

    public static Inferred inferActionParamClasses(LSFInternalActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        List<Inferred> list = new ArrayList<>();

        LSFPropertyExpression propertyExpression = body.getPropertyExpression();
        if(propertyExpression != null) {
            list.add(inferParamClasses(propertyExpression, LSFExClassSet.text).filter(params));
        }

        LSFNonEmptyPropertyExpressionList exParams = body.getNonEmptyPropertyExpressionList();
        if(exParams != null)
            for(LSFPropertyExpression pe : exParams.getPropertyExpressionList())
                list.add(inferParamClasses(pe, LSFExClassSet.text).filter(params));

        return Inferred.orClasses(list);
    }

    public static Inferred inferActionParamClasses(LSFExternalActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        List<Inferred> list = new ArrayList<>();
        
        for(LSFPropertyExpression pe : body.getPropertyExpressionList())
            list.add(inferParamClasses(pe, LSFExClassSet.text).filter(params));

        LSFNonEmptyPropertyExpressionList exParams = body.getNonEmptyPropertyExpressionList();
        if(exParams != null)
            for(LSFPropertyExpression pe : exParams.getPropertyExpressionList())
                list.add(inferParamClasses(pe, LSFExClassSet.text).filter(params));

        return Inferred.orClasses(list);
    }

    public static Inferred inferActionParamClasses(LSFEvalActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return inferExpressionParamClasses(body.getPropertyExpression(), null).filter(params);
    }

    public static Inferred inferActionParamClasses(LSFReadActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFWriteActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }
    
    public static Inferred inferActionParamClasses(LSFImportActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFImportFormActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFNewThreadActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFNewExecutorActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFNewSessionActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return inferActionParamClasses(body.getActionPropertyDefinitionBody(), params);
    }
    
    public static Inferred inferActionParamClasses(LSFDrillDownActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return inferExpressionParamClasses(body.getPropertyExpression(), null).filter(params);
    }

    public static Inferred inferActionParamClasses(LSFApplyActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return inferActionParamClasses(body.getActionPropertyDefinitionBody(), params);
    }
    
    public static Inferred inferActionParamClasses(LSFCancelActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFTryActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {

        List<LSFActionPropertyDefinitionBody> actions = body.getActionPropertyDefinitionBodyList();
        if (actions.isEmpty()) {
            return Inferred.EMPTY;
        }

        Inferred result = inferActionParamClasses(actions.get(0), params);
        if (actions.size() == 2)
            result = result.or(inferActionParamClasses(actions.get(1), params));
        return result;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFNavigatorStatement navigatorStatement, int flags) {
        return LSFIcons.NAVIGATOR_ELEMENT;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFOverridePropertyStatement overrideStatement, int flags) {
        return LSFIcons.OVERRIDE;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFOverrideActionStatement overrideStatement, int flags) {
        return LSFIcons.OVERRIDE;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFConstraintStatement constraintStatement, int flags) {
        return LSFIcons.CONSTRAINT;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFFollowsStatement followsStatement, int flags) {
        return LSFIcons.FOLLOWS;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFWriteWhenStatement writeWhenStatement, int flags) {
        return LSFIcons.WRITE_WHEN;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFEventStatement eventStatement, int flags) {
        return LSFIcons.EVENT;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFShowDepStatement showDepStatement, int flags) {
        return LSFIcons.SHOW_DEP;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFGlobalEventStatement globalEventStatement, int flags) {
        return LSFIcons.EVENT;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFAspectStatement aspectStatement, int flags) {
        return LSFIcons.EVENT;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFLoggableStatement loggableStatement, int flags) {
        return LSFIcons.LOGGABLE;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFIndexStatement indexStatement, int flags) {
        return LSFIcons.INDEX;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFDesignStatement designStatement, int flags) {
        return LSFIcons.Design.DESIGN;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFWindowStatement windowStatement, int flags) {
        return LSFIcons.WINDOW;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFMessagePropertyExpression messagePropertyExpression, int flags) {
        return LSFIcons.MESSAGE;
    }

    //FormContext implementations

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@Nullable LSFFormUsage formUsage) {
        return formUsage == null ? null : formUsage.resolveDecl();
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFFormActionPropertyDefinitionBody formActionBody) {
        return resolveFormDecl(formActionBody.getFormUsage());
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFDialogActionPropertyDefinitionBody formActionBody) {
        return resolveFormDecl(formActionBody.getFormUsage());
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFPrintActionPropertyDefinitionBody formActionBody) {
        return resolveFormDecl(formActionBody.getFormUsage());
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFExportActionPropertyDefinitionBody formActionBody) {
        return resolveFormDecl(formActionBody.getFormUsage());
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFJsonFormPropertyDefinition formActionBody) {
        return resolveFormDecl(formActionBody.getFormUsage());
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFImportFormActionPropertyDefinitionBody formActionBody) {
        return resolveFormDecl(formActionBody.getFormUsage());
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFDesignStatement designStatement) {
        LSFDesignHeader designHeader = designStatement.getDesignHeader();
        return resolveFormDecl(designHeader.getFormUsage());
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFGroupObjectID groupObjectID) {
        return resolveFormDecl(groupObjectID.getFormUsage());
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFObjectID objectID) {
        return resolveFormDecl(objectID.getFormUsage());
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFComponentID objectID) {
        return resolveFormDecl(objectID.getFormUsage());
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFFormPropertyDrawID formPropertyDrawID) {
        return resolveFormDecl(formPropertyDrawID.getFormUsage());
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFFilterGroupID filterGroupID) {
        return resolveFormDecl(filterGroupID.getFormUsage());
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFFormStatement formStatement) {
        return (LSFFormDeclaration) formStatement.resolveDecl();
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFInternalPropertyDraw internalPropertyDraw) {
        return resolveFormDecl(internalPropertyDraw.getFormUsage());
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFInternalFormObject internalFormObject) {
        return resolveFormDecl(internalFormObject.getFormUsage());
    }
    
    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFSeekObjectActionPropertyDefinitionBodyImpl seekObjectActionBody) {
        LSFFormUsage formUsage = null;
        if (seekObjectActionBody.getGroupObjectID() != null) {
            formUsage = seekObjectActionBody.getGroupObjectID().getFormUsage();
        } else if (seekObjectActionBody.getObjectID() != null) {
            formUsage = seekObjectActionBody.getObjectID().getFormUsage();
        }
        return resolveFormDecl(formUsage);
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFExpandGroupObjectActionPropertyDefinitionBodyImpl expandGroupObjectActionBody) {
        LSFFormUsage formUsage = null;
        if (expandGroupObjectActionBody.getGroupObjectID() != null) {
            formUsage = expandGroupObjectActionBody.getGroupObjectID().getFormUsage();
        }
        return resolveFormDecl(formUsage);
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFCollapseGroupObjectActionPropertyDefinitionBodyImpl collapseGroupObjectActionBody) {
        LSFFormUsage formUsage = null;
        if (collapseGroupObjectActionBody.getGroupObjectID() != null) {
            formUsage = collapseGroupObjectActionBody.getGroupObjectID().getFormUsage();
        }
        return resolveFormDecl(formUsage);
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFOrderActionPropertyDefinitionBodyImpl orderActionBody) {
        LSFFormUsage formUsage = orderActionBody.getGroupObjectID().getFormUsage();
        return resolveFormDecl(formUsage);
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFReadOrderActionPropertyDefinitionBodyImpl orderActionBody) {
        LSFFormUsage formUsage = orderActionBody.getGroupObjectID().getFormUsage();
        return resolveFormDecl(formUsage);
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFFilterActionPropertyDefinitionBodyImpl filterActionBody) {
        LSFFormUsage formUsage = filterActionBody.getGroupObjectID().getFormUsage();
        return resolveFormDecl(formUsage);
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFReadFilterActionPropertyDefinitionBodyImpl filterActionBody) {
        LSFFormUsage formUsage = filterActionBody.getGroupObjectID().getFormUsage();
        return resolveFormDecl(formUsage);
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFFilterGroupActionPropertyDefinitionBodyImpl filterActionBody) {
        LSFFormUsage formUsage = filterActionBody.getFilterGroupID().getFormUsage();
        return resolveFormDecl(formUsage);
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFReadFilterGroupsActionPropertyDefinitionBodyImpl filterActionBody) {
        LSFFormUsage formUsage = filterActionBody.getFilterGroupID().getFormUsage();
        return resolveFormDecl(formUsage);
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFFilterPropertyActionPropertyDefinitionBodyImpl filterActionBody) {
        LSFFormPropertyDrawID formPropertyDrawID = filterActionBody.getFormPropertyDrawID();
        if(formPropertyDrawID != null) {
            LSFFormUsage formUsage = formPropertyDrawID.getFormUsage();
            return resolveFormDecl(formUsage);
        } else {
            return null;
        }
    }

    @Nullable
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFReadFiltersPropertyActionPropertyDefinitionBodyImpl filterActionBody) {
        LSFFormPropertyDrawID formPropertyDrawID = filterActionBody.getFormPropertyDrawID();
        if(formPropertyDrawID != null) {
            LSFFormUsage formUsage = formPropertyDrawID.getFormUsage();
            return resolveFormDecl(formUsage);
        } else {
            return null;
        }
    }

    public static boolean checkOverrideValue(@NotNull LSFOverridePropertyStatement o, Result<LSFClassSet> required, Result<LSFClassSet> found) {
        List<LSFPropertyExpression> peList = o.getPropertyExpressionList();
        if(peList.size() >= 1) {
            LSFMappedPropertyClassParamDeclare mappedDeclare = o.getMappedPropertyClassParamDeclare();

            boolean infer = false; // пока не работает, т.к. нет RecursionGuard'а

            LSFExClassSet abstractClass = resolveValueClass(mappedDeclare.getPropertyUsageWrapper().getPropertyUsage(), infer);
            if (abstractClass != null) {
                LSFPropertyExpression pe = peList.get(peList.size() - 1);
                LSFExClassSet peClass = resolveValueClass(pe, infer);
                if(peClass != null) {
                    if(!abstractClass.classSet.containsAll(peClass.classSet, false)) {
                        required.setResult(abstractClass.classSet);
                        found.setResult(peClass.classSet);
                        return false;
                    }
                }
            }
        }

        return true;
    }
    
    public static boolean checkNonRecursiveOverride(@NotNull LSFOverridePropertyStatement override) {
        LSFPropertyUsage leftUsage = override.getMappedPropertyClassParamDeclare().getPropertyUsageWrapper().getPropertyUsage();

        int leftParams = 0;
        LSFClassParamDeclareList classParamDeclareList = override.getMappedPropertyClassParamDeclare().getClassParamDeclareList();
        if(classParamDeclareList != null) {
            LSFNonEmptyClassParamDeclareList nonEmptyClassParamDeclareList = classParamDeclareList.getNonEmptyClassParamDeclareList();
            leftParams = nonEmptyClassParamDeclareList == null ? 0 : nonEmptyClassParamDeclareList.getClassParamDeclareList().size();
        }

        Collection<LSFJoinPropertyDefinition> joinProps = PsiTreeUtil.findChildrenOfType(override, LSFJoinPropertyDefinition.class);
        for (LSFJoinPropertyDefinition joinProp : joinProps) {
            if(!checkNonRecursiveOverrideRecursively(leftUsage, leftParams, joinProp.getPropertyUsage(), joinProp.getParamList(), new HashSet<>()))
                return false;
        }
        return true;
    }

    private static boolean checkNonRecursiveOverrideRecursively(LSFPropertyUsage leftUsage, int leftParams, LSFPropertyUsage rightUsage, PsiElement rightParamList, Set<LSFJoinPropertyDefinition> checkedProps) {
        if (equalReferences(leftUsage, rightUsage, leftParams, rightParamList)) {
            return false;
        }

        if(rightUsage != null) {
            Collection<LSFJoinPropertyDefinition> joinProps = PsiTreeUtil.findChildrenOfType(rightUsage.resolveDecl(), LSFJoinPropertyDefinition.class);
            for (LSFJoinPropertyDefinition joinProp : joinProps) {
                if(checkedProps.add(joinProp)) { //protect from stackoverflow
                    LSFPropertyUsage joinPropUsage = joinProp.getPropertyUsage();
                    if (!equalReferences(rightUsage, joinPropUsage) && !checkNonRecursiveOverrideRecursively(leftUsage, leftParams, joinPropUsage, joinProp.getParamList(), checkedProps)) {
                        return false;
                    }
                }
            }
        }
        //executes too long
        /*for (PsiElement element : rightUsageDecl.processImplementationsSearch()) {
            if (element instanceof LSFPropertyUsageWrapper) {
                LSFOverridePropertyStatement override = PsiTreeUtil.getParentOfType(element, LSFOverridePropertyStatement.class);
                for (LSFJoinPropertyDefinition joinProp : PsiTreeUtil.findChildrenOfType(override, LSFJoinPropertyDefinition.class)) {
                    boolean result = checkNonRecursiveOverrideRecursively(leftUsage, joinProp.getPropertyUsage(), leftParams, joinProp.getParamList());
                    if (!result) {
                        return false;
                    }
                }
            }
        }*/
        return true;
    }

    public static boolean checkNonRecursiveOverride(@NotNull LSFOverrideActionStatement override) {
        LSFActionUsage leftUsage = override.getMappedActionClassParamDeclare().getActionUsageWrapper().getActionUsage();

        int leftParams = 0;
        LSFClassParamDeclareList classParamDeclareList = override.getMappedActionClassParamDeclare().getClassParamDeclareList();
        if(classParamDeclareList != null) {
            LSFNonEmptyClassParamDeclareList nonEmptyClassParamDeclareList = classParamDeclareList.getNonEmptyClassParamDeclareList();
            leftParams = nonEmptyClassParamDeclareList == null ? 0 : nonEmptyClassParamDeclareList.getClassParamDeclareList().size();
        }

        Collection<LSFExecActionPropertyDefinitionBody> execActions = PsiTreeUtil.findChildrenOfType(override, LSFExecActionPropertyDefinitionBody.class);
        for (LSFExecActionPropertyDefinitionBody execAction : execActions) {
            LSFActionUsage rightUsage = execAction.getActionUsage();
            if (equalReferences(leftUsage, rightUsage)) {
                LSFListActionPropertyDefinitionBody rightListAction = override.getListActionPropertyDefinitionBody();
                if (rightListAction != null) {
                    List<LSFActionPropertyDefinitionBody> actions = rightListAction.getActionPropertyDefinitionBodyList();
                    if(actions.size() == 1) {
                        LSFExecActionPropertyDefinitionBody action = actions.get(0).getExecActionPropertyDefinitionBody();
                        if (action != null) {
                            if (equalParams(leftParams, action.getParamList())) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private static boolean equalReferences(@NotNull LSFActionOrPropReference left, LSFActionOrPropReference right) {
        return right != null && Objects.equals(left.resolve(), right.resolve());
    }

    private static boolean equalReferences(@NotNull LSFPropertyUsage left, LSFPropertyUsage right) {
       if(left.equals(right)) return true;
       else {
           LSFPropDeclaration leftDecl = left.resolveDecl();
           return leftDecl != null && right != null && leftDecl.equals(right.resolveDecl());
       }
    }

    private static boolean equalParams(int leftParams, PsiElement paramList) {
        if (paramList instanceof LSFPropertyExpressionListImpl) {
            LSFNonEmptyPropertyExpressionList nonEmptyPropertyExpressionList = ((LSFPropertyExpressionListImpl) paramList).getNonEmptyPropertyExpressionList();
            if (nonEmptyPropertyExpressionList != null) {
                List<LSFPropertyExpression> rightParams = nonEmptyPropertyExpressionList.getPropertyExpressionList();
                if(leftParams == rightParams.size()) {
                    boolean equalParams = true;
                    for (LSFPropertyExpression propertyExpression : rightParams) {
                        if (!PsiTreeUtil.findChildrenOfType(propertyExpression, LSFExpressionFriendlyPD.class).isEmpty()) {
                            equalParams = false;
                            break;
                        }
                    }
                    return equalParams;
                }
            }
        }
        return false;
    }

    private static boolean equalReferences(@NotNull LSFActionOrPropReference left, LSFActionOrPropReference right, int leftParams, PsiElement paramList) {
        return equalReferences(left, right) && equalParams(leftParams, paramList);
    }

    // Documentation

    public static String getDocumentation(LSFModuleHeader moduleHeader, PsiElement child) {
        return "Module_header";
    }

    public static String getDocumentation(LSFClassDecl lsfClassDecl, PsiElement child) {
        return "CLASS_statement";
    }

    public static String getDocumentation(LSFExtendingClassDeclaration lsfExtendingClassDeclaration, PsiElement child) {
        return "EXTEND_CLASS_statement";
    }

    public static String getDocumentation(LSFClassStatement lsfClassStatement, PsiElement child) {
        return "CLASS_statement";
    }

    public static String getDocumentation(LSFGroupStatement lsfGroupStatement, PsiElement child) {
        return "GROUP_statement";
    }


    public static String getDocumentation(LSFPropertyStatement lsfPropertyStatement, PsiElement child) {
        return "Properties";
    }

    public static String getDocumentation(LSFActionStatement lsfActionStatement, PsiElement child) {
        return "ACTION_statement";
    }

    //use only with LSFExpression instances
    private static String getLSFExpressionDocumentation(LSFExpression parent, PsiElement child, String documentation) {
        return parent.getChildren().length > 1 && parent.getChildren()[0] != child ? documentation : null;
    }

    public static String getDocumentation(LSFIfPE lsfIfPE, PsiElement child) {
        return getLSFExpressionDocumentation(lsfIfPE, child, "IF_operator");
    }

    public static String getDocumentation(LSFOrPE lsfOrPE, PsiElement child) {
        return getLSFExpressionDocumentation(lsfOrPE, child, "AND_OR_NOT_XOR_operators");
    }

    public static String getDocumentation(LSFXorPE lsfXorPE, PsiElement child) {
        return getLSFExpressionDocumentation(lsfXorPE, child, "AND_OR_NOT_XOR_operators");
    }

    public static String getDocumentation(LSFAndPE lsfAndPE, PsiElement child) {
        return getLSFExpressionDocumentation(lsfAndPE, child, "AND_OR_NOT_XOR_operators");
    }

    public static String getDocumentation(LSFNotPE lsfNotPE, PsiElement child) {
        return getLSFExpressionDocumentation(lsfNotPE, child, "AND_OR_NOT_XOR_operators");
    }

    public static String getDocumentation(LSFEqualityPE lsfEqualityPE, PsiElement child) {
        return getLSFExpressionDocumentation(lsfEqualityPE, child, "Comparison_operators_=_etc");
    }

    public static String getDocumentation(LSFRelationalPE lsfRelationalPE, PsiElement child) {
        return getLSFExpressionDocumentation(lsfRelationalPE, child, "Comparison_operators_=_etc");
    }

    public static String getDocumentation(LSFLikePE lsfLikePE, PsiElement child) {
        return getLSFExpressionDocumentation(lsfLikePE, child, "Arithmetic_operators_+_-_etc");
    }

    public static String getDocumentation(LSFAdditiveORPE lsfAdditiveORPE, PsiElement child) {
        return getLSFExpressionDocumentation(lsfAdditiveORPE, child, "Arithmetic_operators_+_-_etc");
    }

    public static String getDocumentation(LSFAdditivePE lsfAdditivePE, PsiElement child) {
        return getLSFExpressionDocumentation(lsfAdditivePE, child, "Arithmetic_operators_+_-_etc");
    }

    public static String getDocumentation(LSFMultiplicativePE lsfMultiplicativePE, PsiElement child) {
        return getLSFExpressionDocumentation(lsfMultiplicativePE, child, "Arithmetic_operators_+_-_etc");
    }

    public static String getDocumentation(LSFAggrPropertyDefinition lsfAggrPropertyDefinition, PsiElement child) {
        return "AGGR_operator";
    }

    public static String getDocumentation(LSFOverrideOperator lsfOverrideOperator, PsiElement child) {
        return "OVERRIDE_operator";
    }

    public static String getDocumentation(LSFExclusiveOperator lsfExclusiveOperator, PsiElement child) {
        return "EXCLUSIVE_operator";
    }

    public static String getDocumentation(LSFIfElsePropertyDefinition lsfIfElsePropertyDefinition, PsiElement child) {
        return "IF_..._THEN_operator";
    }

    public static String getDocumentation(LSFMaxPropertyDefinition lsfMaxPropertyDefinition, PsiElement child) {
        return "Extremum_MAX_MIN";
    }

    public static String getDocumentation(LSFCasePropertyDefinition lsfCasePropertyDefinition, PsiElement child) {
        return "CASE_operator";
    }

    public static String getDocumentation(LSFMultiPropertyDefinition lsfMultiPropertyDefinition, PsiElement child) {
        return "MULTI_operator";
    }

    public static String getDocumentation(LSFPartitionPropertyDefinition lsfPartitionPropertyDefinition, PsiElement child) {
        return "PARTITION_operator";
    }

    public static String getDocumentation(LSFRecursivePropertyDefinition lsfRecursivePropertyDefinition, PsiElement child) {
        return "RECURSION_operator";
    }

    public static String getDocumentation(LSFStructCreationPropertyDefinition lsfStructCreationPropertyDefinition, PsiElement child) {
        return "STRUCT_operator";
    }

    public static String getDocumentation(LSFConcatPropertyDefinition lsfConcatPropertyDefinition, PsiElement child) {
        return "CONCAT_operator";
    }

    public static String getDocumentation(LSFSessionPropertyDefinition lsfSessionPropertyDefinition, PsiElement child) {
        return "Change_operators_SET_CHANGED_etc";
    }

    public static String getDocumentation(LSFActiveTabPropertyDefinition lsfActiveTabPropertyDefinition, PsiElement child) {
        return "ACTIVE_TAB_operator";
    }

    public static String getDocumentation(LSFDataPropertyDefinition lsfDataPropertyDefinition, PsiElement child) {
        return "DATA_operator";
    }

    public static String getDocumentation(LSFAbstractPropertyDefinition lsfAbstractPropertyDefinition, PsiElement child) {
        return "ABSTRACT_operator";
    }

    public static String getDocumentation(LSFFormulaPropertyDefinition lsfFormulaPropertyDefinition, PsiElement child) {
        return "FORMULA_operator";
    }

    public static String getDocumentation(LSFGroupPropertyDefinition lsfGroupPropertyDefinition, PsiElement child) {
        return "GROUP_operator";
    }

    public static String getDocumentation(LSFGroupExprPropertyDefinition lsfGroupExprPropertyDefinition, PsiElement child) {
        return "GROUP_operator";
    }

    public static String getDocumentation(LSFFilterPropertyDefinition lsfFilterPropertyDefinition, PsiElement child) {
        return "Object_group_operator";
    }

    public static String getDocumentation(LSFTypePropertyDefinition lsfTypePropertyDefinition, PsiElement child) {
        return "IS_AS_operators";
    }

    public static String getDocumentation(LSFNonEmptyPropertyOptions lsfNonEmptyPropertyOptions, PsiElement child) {
        return "Property_options";
    }

    public static String getDocumentation(LSFAbstractActionPropertyDefinition lsfAbstractActionPropertyDefinition, PsiElement child) {
        return "ABSTRACT_action_operator";
    }

    public static String getDocumentation(LSFObjectInputProps lsfObjectInputProps, PsiElement child) {
        return "INPUT_operator";
    }

    public static String getDocumentation(LSFFormActionPropertyDefinitionBody lsfFormActionPropertyDefinitionBody, PsiElement child) {
        return "SHOW_operator";
    }

    public static String getDocumentation(LSFDialogActionPropertyDefinitionBody lsfDialogActionPropertyDefinitionBody, PsiElement child) {
        return "DIALOG_operator";
    }

    public static String getDocumentation(LSFPrintActionPropertyDefinitionBody lsfPrintActionPropertyDefinitionBody, PsiElement child) {
        return "PRINT_operator";
    }

    public static String getDocumentation(LSFExportActionPropertyDefinitionBody lsfExportActionPropertyDefinitionBody, PsiElement child) {
        return "EXPORT_operator";
    }

    public static String getDocumentation(LSFExportDataActionPropertyDefinitionBody lsfExportDataActionPropertyDefinitionBody, PsiElement child) {
        return "Data_export_EXPORT";
    }

    public static String getDocumentation(LSFJsonFormPropertyDefinition lsfJsonFormPropertyDefinitionBody, PsiElement child) {
        return "JSON_operator";
    }

    public static String getDocumentation(LSFJsonPropertyDefinition lsfJsonPropertyDefinition, PsiElement child) {
        return "JSON_operator";
    }

    public static String getDocumentation(LSFCustomActionPropertyDefinitionBody lsfCustomActionPropertyDefinitionBody, PsiElement child) {
        return "INTERNAL_operator";
    }

    public static String getDocumentation(LSFInternalActionPropertyDefinitionBody lsfExternalActionPropertyDefinitionBody, PsiElement child) {
        return "INTERNAL_operator";
    }

    public static String getDocumentation(LSFExternalActionPropertyDefinitionBody lsfExternalActionPropertyDefinitionBody, PsiElement child) {
        return "EXTERNAL_operator";
    }

    public static String getDocumentation(LSFNewWhereActionPropertyDefinitionBody lsfNewWhereActionPropertyDefinitionBody, PsiElement child) {
        return "NEW_operator";
    }

    public static String getDocumentation(LSFEmailActionPropertyDefinitionBody lsfEmailActionPropertyDefinitionBody, PsiElement child) {
        return "EMAIL_operator";
    }

    public static String getDocumentation(LSFConfirmActionPropertyDefinitionBody lsfConfirmActionPropertyDefinitionBody, PsiElement child) {
        return "ASK_operator";
    }

    public static String getDocumentation(LSFMessageActionPropertyDefinitionBody lsfMessageActionPropertyDefinitionBody, PsiElement child) {
        return "MESSAGE_operator";
    }

    public static String getDocumentation(LSFSeekObjectActionPropertyDefinitionBody lsfSeekObjectActionPropertyDefinitionBody, PsiElement child) {
        return "SEEK_operator";
    }

    public static String getDocumentation(LSFChangeClassActionPropertyDefinitionBody lsfChangeClassActionPropertyDefinitionBody, PsiElement child) {
        return "CHANGECLASS_operator";
    }

    public static String getDocumentation(LSFDeleteActionPropertyDefinitionBody lsfDeleteActionPropertyDefinitionBody, PsiElement child) {
        return "DELETE_operator";
    }

    public static String getDocumentation(LSFEvalActionPropertyDefinitionBody lsfEvalActionPropertyDefinitionBody, PsiElement child) {
        return "EVAL_operator";
    }

    public static String getDocumentation(LSFReadActionPropertyDefinitionBody lsfReadActionPropertyDefinitionBody, PsiElement child) {
        return "READ_operator";
    }

    public static String getDocumentation(LSFWriteActionPropertyDefinitionBody lsfWriteActionPropertyDefinitionBody, PsiElement child) {
        return "WRITE_operator";
    }

    public static String getDocumentation(LSFImportActionPropertyDefinitionBody lsfImportActionPropertyDefinitionBody, PsiElement child) {
        return "IMPORT_operator";
    }

    public static String getDocumentation(LSFImportFormActionPropertyDefinitionBody lsfImportFormActionPropertyDefinitionBody, PsiElement child) {
        return "IMPORT_operator";
    }

    public static String getDocumentation(LSFNewThreadActionPropertyDefinitionBody lsfNewThreadActionPropertyDefinitionBody, PsiElement child) {
        return "NEWTHREAD_operator";
    }

    public static String getDocumentation(LSFNewExecutorActionPropertyDefinitionBody lsfNewExecutorActionPropertyDefinitionBody, PsiElement child) {
        return "NEWEXECUTOR_operator";
    }

    public static String getDocumentation(LSFNewSessionOperator lsfNewSessionOperator, PsiElement child) {
        return "NEWSESSION_operator";
    }

    public static String getDocumentation(LSFNestedSessionOperator lsfNestedSessionOperator, PsiElement child) {
        return "NESTEDSESSION_operator";
    }

    public static String getDocumentation(LSFRequestActionPropertyDefinitionBody lsfRequestActionPropertyDefinitionBody, PsiElement child) {
        return "REQUEST_operator";
    }

    public static String getDocumentation(LSFInputActionPropertyDefinitionBody lsfInputActionPropertyDefinitionBody, PsiElement child) {
        return "INPUT_operator";
    }

    public static String getDocumentation(LSFActiveFormActionPropertyDefinitionBody lsfActiveFormActionPropertyDefinitionBody, PsiElement child) {
        return "ACTIVE_FORM_operator";
    }

    public static String getDocumentation(LSFActivateActionPropertyDefinitionBody lsfActivateActionPropertyDefinitionBody, PsiElement child) {
        return "ACTIVATE_operator";
    }

    public static String getDocumentation(LSFListActionPropertyDefinitionBody lsfListActionPropertyDefinitionBody, PsiElement child) {
        return "Braces_operator";
    }

    public static String getDocumentation(LSFLocalDataPropertyDefinition lsfLocalDataPropertyDefinition, PsiElement child) {
        return "Braces_operator";
    }

    public static String getDocumentation(LSFExecActionPropertyDefinitionBody lsfExecActionPropertyDefinitionBody, PsiElement child) {
        return "EXEC_operator";
    }

    public static String getDocumentation(LSFAssignActionPropertyDefinitionBody lsfAssignActionPropertyDefinitionBody, PsiElement child) {
        return "CHANGE_operator";
    }

    public static String getDocumentation(LSFTryActionPropertyDefinitionBody lsfTryActionPropertyDefinitionBody, PsiElement child) {
        return "TRY_operator";
    }

    public static String getDocumentation(LSFIfActionPropertyDefinitionBody lsfIfActionPropertyDefinitionBody, PsiElement child) {
        return "IF_..._THEN_operator";
    }

    public static String getDocumentation(LSFCaseActionPropertyDefinitionBody lsfCaseActionPropertyDefinitionBody, PsiElement child) {
        return "CASE_action_operator";
    }

    public static String getDocumentation(LSFMultiActionPropertyDefinitionBody lsfMultiActionPropertyDefinitionBody, PsiElement child) {
        return "MULTI_action_operator";
    }

    public static String getDocumentation(LSFApplyActionPropertyDefinitionBody lsfApplyActionPropertyDefinitionBody, PsiElement child) {
        return "APPLY_operator";
    }

    public static String getDocumentation(LSFCancelActionPropertyDefinitionBody lsfCancelActionPropertyDefinitionBody, PsiElement child) {
        return "CANCEL_operator";
    }

    public static String getDocumentation(LSFNewActionPropertyDefinitionBody lsfNewActionPropertyDefinitionBody, PsiElement child) {
        return "NEW_operator";
    }

    public static String getDocumentation(LSFForActionPropertyMainBody lsfForActionPropertyMainBody, PsiElement child) {
        return "FOR_operator";
    }

    public static String getDocumentation(LSFWhileActionPropertyDefinitionBody lsfWhileActionPropertyDefinitionBody, PsiElement child) {
        return "WHILE_operator";
    }

    public static String getDocumentation(LSFBreakActionOperator lsfBreakActionOperator, PsiElement child) {
        return "BREAK_operator";
    }

    public static String getDocumentation(LSFContinueActionOperator lsfBreakActionOperator, PsiElement child) {
        return "CONTINUE_operator";
    }

    public static String getDocumentation(LSFReturnActionOperator lsfReturnActionOperator, PsiElement child) {
        return "RETURN_operator";
    }

    public static String getDocumentation(LSFListFormDeclaration listFormDeclaration, PsiElement child) {
        return "Properties_and_actions_block";
    }

    public static String getDocumentation(LSFEditFormDeclaration lsfEditFormDeclaration, PsiElement child) {
        return "Properties_and_actions_block";
    }

    public static String getDocumentation(LSFReportSetting lsfReportSetting, PsiElement child) {
        return "FORM_statement";
    }

    public static String getDocumentation(LSFFormExtIDSetting lsfFormExtIDSetting, PsiElement child) {
        return "FORM_statement";
    }

    public static String getDocumentation(LSFFormDecl lsfFormDecl, PsiElement child) {
        return "FORM_statement";
    }

    public static String getDocumentation(LSFExtendingFormDeclaration lsfExtendingFormDeclaration, PsiElement child) {
        return "EXTEND_FORM_statement";
    }

    public static String getDocumentation(LSFFormGroupObjectsList lsfFormGroupObjectsList, PsiElement child) {
        return "Object_blocks";
    }

    public static String getDocumentation(LSFFormTreeGroupObjectList lsfFormTreeGroupObjectList, PsiElement child) {
        return "Object_blocks";
    }

    public static String getDocumentation(LSFFormGroupObjectOptions lsfFormGroupObjectOptions, PsiElement child) {
        return "Object_blocks";
    }

    public static String getDocumentation(LSFFormPropertiesList lsfFormPropertiesList, PsiElement child) {
        return "Properties_and_actions_block";
    }

    public static String getDocumentation(LSFFormOptionsOnEvents lsfFormOptionsOnEvents, PsiElement child) {
        return "Form_events";
    }

    public static String getDocumentation(LSFFormFiltersList lsfFormFiltersList, PsiElement child) {
        return "Filters_and_sortings_block";
    }

    public static String getDocumentation(LSFFormEventsList lsfFormEventsList, PsiElement child) {
        return "Event_block";
    }

    public static String getDocumentation(LSFFormFilterGroupDeclaration lsfFormFilterGroupDeclaration, PsiElement child) {
        return "Filters_and_sortings_block";
    }

    public static String getDocumentation(LSFFormExtendFilterGroupDeclaration lsfFormExtendFilterGroupDeclaration, PsiElement child) {
        return "Filters_and_sortings_block";
    }

    public static String getDocumentation(LSFFormOrderByList lsfFormOrderByList, PsiElement child) {
        return "Filters_and_sortings_block";
    }

    public static String getDocumentation(LSFFormPivotOptionsDeclaration lsfFormPivotOptionsDeclaration, PsiElement child) {
        return "Properties_and_actions_block";
    }

    public static String getDocumentation(LSFOverrideActionStatement lsfOverrideActionStatement, PsiElement child) {
        return "ACTION+_statement";
    }

    public static String getDocumentation(LSFOverridePropertyStatement lsfOverridePropertyStatement, PsiElement child) {
        return "+=_statement";
    }

    public static String getDocumentation(LSFConstraintStatement lsfConstraintStatement, PsiElement child) {
        return "CONSTRAINT_statement";
    }

    public static String getDocumentation(LSFFollowsStatement lsfFollowsStatement, PsiElement child) {
        return "=gt_statement";
    }

    public static String getDocumentation(LSFWriteWhenStatement lsfWriteWhenStatement, PsiElement child) {
        return "lt-_WHEN_statement";
    }

    public static String getDocumentation(LSFEventStatement lsfEventStatement, PsiElement child) {
        return "WHEN_statement";
    }

    public static String getDocumentation(LSFGlobalEventStatement lsfGlobalEventStatement, PsiElement child) {
        return "ON_statement";
    }

    public static String getDocumentation(LSFAspectBefore lsfAspectBefore, PsiElement child) {
        return "BEFORE_statement";
    }

    public static String getDocumentation(LSFAspectAfter lsfAspectAfter, PsiElement child) {
        return "AFTER_statement";
    }

    public static String getDocumentation(LSFTableStatement lsfTableStatement, PsiElement child) {
        return "TABLE_statement";
    }

    public static String getDocumentation(LSFIndexStatement lsfIndexStatement, PsiElement child) {
        return "INDEX_statement";
    }

    public static String getDocumentation(LSFWindowCreateStatement lsfWindowCreateStatement, PsiElement child) {
        return "WINDOW_statement";
    }

    public static String getDocumentation(LSFNavigatorStatement lsfNavigatorStatement, PsiElement child) {
        return "NAVIGATOR_statement";
    }

    public static String getDocumentation(LSFDesignStatement lsfDesignStatement, PsiElement child) {
        return "DESIGN_statement";
    }

    public static String getDocumentation(LSFMetaCodeDeclarationStatement lsfMetaCodeDeclarationStatement, PsiElement child) {
        return "META_statement";
    }

    public static String getDocumentation(LSFLiteral lsfLiteral, PsiElement child) {
        return "Literals";
    }

    public static String getDocumentation(LSFExpressionLiteral literal, PsiElement child) {
        return "Literals";
    }

    public static String getDocumentation(LSFBuiltInClassName lsfBuiltInClassName, PsiElement child) {
        return "Built-in_classes";
    }

    public static String getDocumentation(LSFMetacodeUsage lsfMetacodeUsage, PsiElement child) {
        return "commat_statement";
    }

    public static String getDocumentation(LSFExclusiveOverrideOption lsfExclusiveOverrideOption, PsiElement child) {
        return "Selection_CASE_IF_MULTI_OVERRIDE_EXCLUSIVE";
    }

    public static String getDocumentation(LSFAbstractExclusiveOverrideOption lsfAbstractExclusiveOverrideOption, PsiElement child) {
        return "Selection_CASE_IF_MULTI_OVERRIDE_EXCLUSIVE";
    }

    public static String getDocumentation(LSFEqualsSign lsfEqualsSign, PsiElement child) {
        return "=_statement";
    }
}
