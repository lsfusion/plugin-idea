package com.simpleplugin;

import com.intellij.lang.ASTFactory;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.simpleplugin.classes.*;
import com.simpleplugin.meta.MetaTransaction;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.context.*;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;
import com.simpleplugin.psi.declarations.LSFParamDeclaration;
import com.simpleplugin.psi.declarations.LSFPropDeclaration;
import com.simpleplugin.psi.references.LSFAbstractParamReference;
import com.simpleplugin.psi.references.LSFExprParamReference;
import com.simpleplugin.typeinfer.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public class LSFPsiImplUtil {

    public static ContextModifier getContextModifier(@NotNull LSFPropertyStatement sourceStatement) {
        LSFClassParamDeclareList decl = sourceStatement.getPropertyDeclaration().getClassParamDeclareList();
        if (decl != null)
            return new ExplicitContextModifier(decl);

        LSFPropertyExpression expression = sourceStatement.getPropertyExpression();
        if (expression != null)
            return new ExprsContextModifier(expression);

        return ContextModifier.EMPTY;
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFPropertyStatement sourceStatement) {
        LSFPropertyExpression expression = sourceStatement.getPropertyExpression();
        if (expression != null)
            return new ExprsContextInferrer(expression);
        else {
            LSFExpressionUnfriendlyPD unfriendlyPD = sourceStatement.getExpressionUnfriendlyPD();
            if (unfriendlyPD != null) {
                LSFActionPropertyDefinition action = unfriendlyPD.getActionPropertyDefinition();
                if (action != null) {
                    return new ActionInferrer(action);
                }
            }
            return ContextInferrer.EMPTY;
        }
    }

    public static ContextModifier getContextModifier(@NotNull LSFOverrideStatement sourceStatement) {
        return new ExplicitContextModifier(sourceStatement.getMappedPropertyClassParamDeclare());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFOverrideStatement sourceStatement) {
        ContextInferrer implicit;

        List<LSFPropertyExpression> peList = sourceStatement.getPropertyExpressionList();
        implicit = new ExprsContextInferrer(peList);

        LSFActionPropertyDefinition action = sourceStatement.getActionPropertyDefinition();
        if (action != null) {
            ActionExprInferrer actionInfer = new ActionExprInferrer(action.getActionPropertyDefinitionBody());
            if (peList.isEmpty())
                implicit = actionInfer;
            else
                implicit = new OverrideContextInferrer(actionInfer, implicit);
        }
        return implicit;
    }

    public static ContextModifier getContextModifier(@NotNull LSFActionPropertyDefinition sourceStatement) {
        LSFExprParameterUsageList decl = sourceStatement.getExprParameterUsageList();
        if (decl != null)
            return new ExprParamUsageContextModifier(decl.getExprParameterUsageList());
        return ContextModifier.EMPTY;
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFActionPropertyDefinition sourceStatement) {
        return new ActionExprInferrer(sourceStatement.getActionPropertyDefinitionBody());
    }

    public static ContextModifier getContextModifier(@NotNull LSFConstraintStatement sourceStatement) {
        LSFPropertyExpression decl = sourceStatement.getPropertyExpression();
        if (decl == null)
            return ContextModifier.EMPTY;
        return new ExprsContextModifier(decl);
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFConstraintStatement sourceStatement) {
        LSFPropertyExpression decl = sourceStatement.getPropertyExpression();
        if (decl == null)
            return ContextInferrer.EMPTY;
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
        LSFMappedPropertyClassParamDeclare decl = sourceStatement.getMappedPropertyClassParamDeclare();
        if (decl == null)
            return ContextModifier.EMPTY;
        return new ExplicitContextModifier(decl);
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFAspectStatement sourceStatement) {
        LSFMappedPropertyClassParamDeclare decl = sourceStatement.getMappedPropertyClassParamDeclare();
        if (decl == null)
            return ContextInferrer.EMPTY;
        return new MappedPropertyContextInferrer(decl);
    }

    private static List<LSFPropertyExpression> getContextExprs(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        List<LSFPropertyExpression> result = new ArrayList<LSFPropertyExpression>();
        result.addAll(sourceStatement.getNonEmptyPropertyExpressionList().getPropertyExpressionList());
        LSFGroupPropertyBy by = sourceStatement.getGroupPropertyBy();
        if (by != null)
            result.addAll(by.getNonEmptyPropertyExpressionList().getPropertyExpressionList());
        LSFOrderPropertyBy orderBy = sourceStatement.getOrderPropertyBy();
        if (orderBy != null)
            result.addAll(orderBy.getNonEmptyPropertyExpressionList().getPropertyExpressionList());
        LSFPropertyExpression pe = sourceStatement.getPropertyExpression();
        if (pe != null)
            result.add(pe);
        return result;
    }

    public static ContextModifier getContextModifier(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        return new ExprsContextModifier(getContextExprs(sourceStatement));
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        return new ExprsContextInferrer(getContextExprs(sourceStatement));
    }

    public static ContextModifier getContextModifier(@NotNull LSFPropertyExprObject sourceStatement) {
        LSFPropertyExpression expression = sourceStatement.getPropertyExpression();
        if (expression != null)
            return new ExprsContextModifier(expression);
        return ContextModifier.EMPTY;
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFPropertyExprObject sourceStatement) {
        LSFPropertyExpression expression = sourceStatement.getPropertyExpression();
        if (expression != null)
            return new ExprsContextInferrer(expression);
        return ContextInferrer.EMPTY;
    }

    public static ContextModifier getContextModifier(@NotNull LSFForActionPropertyDefinitionBody sourceStatement) {
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
        return result;
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFForActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getContextModifier(@NotNull LSFWhileActionPropertyDefinitionBody sourceStatement) {
        return new ExprsContextModifier(sourceStatement.getPropertyExpression());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFWhileActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getContextModifier(@NotNull LSFAssignActionPropertyDefinitionBody sourceStatement) {
        return new ExprParamUsageContextModifier(sourceStatement.getMappedPropertyExprParam().getExprParameterUsageList().getExprParameterUsageList());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFAssignActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getContextModifier(@NotNull LSFChangeClassActionPropertyDefinitionBody sourceStatement) {
        return new ExprParamUsageContextModifier(Collections.singletonList(sourceStatement.getExprParameterUsage()));
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFChangeClassActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getContextModifier(@NotNull LSFDeleteActionPropertyDefinitionBody sourceStatement) {
        return new ExprParamUsageContextModifier(Collections.singletonList(sourceStatement.getExprParameterUsage()));
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFDeleteActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getContextModifier(@NotNull LSFAddObjectActionPropertyDefinitionBody sourceStatement) {
        LSFPropertyExpression expression = sourceStatement.getPropertyExpression();
        if (expression == null)
            return ContextModifier.EMPTY;
        return new ExprsContextModifier(sourceStatement.getPropertyExpression());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFAddObjectActionPropertyDefinitionBody sourceStatement) {
        return ContextInferrer.EMPTY;
    }

    // CLASSES

    public static LSFClassSet op(@Nullable LSFClassSet class1, @Nullable LSFClassSet class2, boolean or) {
        if (class1 == null || class2 == null) {
            LSFClassSet nvlClass = BaseUtils.nvl(class1, class2);
            if (or && !(nvlClass instanceof DataClass))
                return null;
            else
                return nvlClass;
        }

        if (class1 instanceof DataClass) {
            if (class2 instanceof DataClass)
                return ((DataClass) class1).op((DataClass) class2, or);
            return null;
        }
        if (class2 instanceof DataClass)
            return null;

        return ((CustomClassSet) class1).op((CustomClassSet) class2, or);
    }

    @Nullable
    private static LSFClassSet or(@Nullable LSFClassSet class1, @Nullable LSFClassSet class2) {
        return op(class1, class2, true);
    }

    public static boolean containsAll(@NotNull List<LSFClassSet> who, @NotNull List<LSFClassSet> what, boolean falseImplicitClass) {
        for (int i = 0, size = who.size(); i < size; i++) {
            LSFClassSet whoClass = who.get(i);
            LSFClassSet whatClass = what.get(i);
            if (whoClass == null && whatClass == null)
                continue;

            if (whoClass != null && whatClass != null && !whoClass.containsAll(whatClass))
                return false;

            if (whatClass != null)
                continue;

            if (falseImplicitClass)
                return false;
        }
        return true;
    }

    public static boolean haveCommonChilds(@NotNull List<LSFClassSet> classes1, @NotNull List<LSFClassSet> classes2) {
        for (int i = 0, size = classes1.size(); i < size; i++) {
            LSFClassSet class1 = classes1.get(i);
            LSFClassSet class2 = classes2.get(i);
            if (class1 != null && class2 != null && !class1.haveCommonChilds(class2)) // потом переделать haveCommonChilds
                return false;
        }
        return true;
    }

    public static int getCommonChildrenCount(@NotNull List<LSFClassSet> classes1, @NotNull List<LSFClassSet> classes2) {
        int count = 0;
        for (int i = 0, size = Math.min(classes1.size(), classes2.size()); i < size; i++) {
            LSFClassSet class1 = classes1.get(i);
            LSFClassSet class2 = classes2.get(i);
            if (class1 == null || class2 == null || class1.haveCommonChilds(class2)) { // потом переделать haveCommonChilds
                count++;
            }
        }
        return count;
    }

    public static boolean allClassesDeclared(List<LSFClassSet> classes) {
        for (LSFClassSet classSet : classes)
            if (classSet == null)
                return false;
        return true;
    }

    private static LSFClassSet resolve(LSFBuiltInClassName builtIn) {
        String name = builtIn.getText();
        if (name.equals("DOUBLE"))
            return new DoubleClass();
        if (name.equals("INTEGER"))
            return new IntegerClass();
        if (name.equals("LONG"))
            return new LongClass();
        if (name.equals("YEAR"))
            return new YearClass();

        if (name.startsWith("STRING[")) {
            name = name.substring("STRING[".length(), name.length() - 1);
            return new StringClass(true, false, new ExtInt(Integer.parseInt(name)));
        } else if (name.startsWith("ISTRING[")) {
            name = name.substring("ISTRING[".length(), name.length() - 1);
            return new StringClass(true, true, new ExtInt(Integer.parseInt(name)));
        } else if (name.startsWith("VARSTRING[")) {
            name = name.substring("VARSTRING[".length(), name.length() - 1);
            return new StringClass(false, false, new ExtInt(Integer.parseInt(name)));
        } else if (name.startsWith("VARISTRING[")) {
            name = name.substring("VARISTRING[".length(), name.length() - 1);
            return new StringClass(false, true, new ExtInt(Integer.parseInt(name)));
        } else if (name.startsWith("NUMERIC[")) {
            String length = name.substring("NUMERIC[".length(), name.indexOf(","));
            String precision = name.substring(name.indexOf(",") + 1, name.length() - 1);
            return new NumericClass(Integer.parseInt(length), Integer.parseInt(precision));
        }

        return new SimpleDataClass(name);
    }

    public static LSFClassSet resolveClass(LSFCustomClassUsage usage) {
        LSFClassDeclaration decl = usage.resolveDecl();
        if (decl == null)
            return null;
        return new CustomClassSet(decl);
    }

    @Nullable
    public static LSFClassSet resolveClass(LSFClassName className) {
        LSFBuiltInClassName builtInClassName = className.getBuiltInClassName();
        if (builtInClassName != null)
            return resolve(builtInClassName);
        return resolveClass(className.getCustomClassUsage());
    }

    @NotNull
    public static List<LSFClassSet> resolveClass(LSFClassNameList classNameList) {
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();

        LSFNonEmptyClassNameList ne = classNameList.getNonEmptyClassNameList();
        if (ne == null)
            return result;
        for (LSFClassName className : ne.getClassNameList())
            result.add(resolveClass(className));
        return result;
    }

    @Nullable
    public static LSFClassSet resolveClass(@NotNull LSFClassParamDeclare sourceStatement) {
        LSFClassName className = sourceStatement.getClassName();
        if (className == null)
            return null;
        return resolveClass(className);
    }

    public static List<LSFClassSet> resolveClass(LSFClassParamDeclareList classNameList) {
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();

        LSFNonEmptyClassParamDeclareList ne = classNameList.getNonEmptyClassParamDeclareList();
        if (ne == null)
            return result;
        for (LSFClassParamDeclare classParamDeclare : ne.getClassParamDeclareList())
            result.add(resolveClass(classParamDeclare));
        return result;
    }

    @Nullable
    public static LSFClassSet resolveClass(@NotNull LSFForAddObjClause sourceStatement) {
        return resolveClass(sourceStatement.getCustomClassUsage());
    }

    // PROPERTYEXPRESSION.RESOLVEVALUECLASS

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFPropertyExpression sourceStatement, boolean infer) {
        return sourceStatement.resolveInferredValueClass(infer ? sourceStatement.inferParamClasses(null).finish() : null);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFPropertyExpression sourceStatement, @Nullable InferResult inferred) {
        return resolveInferredValueClass(sourceStatement.getIfPE(), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFIfPE sourceStatement, @Nullable InferResult inferred) {
        return resolveInferredValueClass(sourceStatement.getOrPEList().get(0), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFOrPE sourceStatement, @Nullable InferResult inferred) {
        List<LSFXorPE> list = sourceStatement.getXorPEList();
        if (list.size() > 1)
            return DataClass.BOOLEAN;
        return resolveInferredValueClass(list.get(0), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFXorPE sourceStatement, @Nullable InferResult inferred) {
        List<LSFAndPE> list = sourceStatement.getAndPEList();
        if (list.size() > 1)
            return DataClass.BOOLEAN;
        return resolveInferredValueClass(list.get(0), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFAndPE sourceStatement, @Nullable InferResult inferred) {
        List<LSFNotPE> list = sourceStatement.getNotPEList();
        if (list.size() > 1)
            return DataClass.BOOLEAN;
        return resolveInferredValueClass(list.get(0), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFNotPE sourceStatement, @Nullable InferResult inferred) {
        LSFEqualityPE eqPE = sourceStatement.getEqualityPE();
        if (eqPE != null)
            return resolveInferredValueClass(eqPE, inferred);
        return DataClass.BOOLEAN;
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFEqualityPE sourceStatement, @Nullable InferResult inferred) {
        List<LSFRelationalPE> list = sourceStatement.getRelationalPEList();
        if (list.size() == 2)
            return DataClass.BOOLEAN;
        return resolveInferredValueClass(list.get(0), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFRelationalPE sourceStatement, @Nullable InferResult inferred) {
        List<LSFAdditiveORPE> list = sourceStatement.getAdditiveORPEList();
        if (list.size() == 2)
            return DataClass.BOOLEAN;

        LSFClassSet result = resolveInferredValueClass(list.get(0), inferred);

        LSFTypePropertyDefinition typeDef = sourceStatement.getTypePropertyDefinition();
        if (typeDef != null) {
            if (typeDef.getTypeIs().getText().equals("IS"))
                return DataClass.BOOLEAN;
            else {
                LSFClassSet resolveClass = resolveClass(typeDef.getClassName());
                if (resolveClass != null) {
                    if (result == null)
                        result = resolveClass;
                    else
                        result = result.op(resolveClass, false);
                }
            }
        }
        return result;
    }

    private static LSFClassSet orClasses(List<LSFClassSet> classes) {
        LSFClassSet result = null;
        for (int i = 0; i < classes.size(); i++) {
            LSFClassSet classSet = classes.get(i);
            if (i == 0)
                result = classSet;
            else
                result = or(result, classSet);
        }
        return result;
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFAdditiveORPE sourceStatement, @Nullable InferResult inferred) {
        List<LSFClassSet> orClasses = new ArrayList<LSFClassSet>();
        for (LSFAdditivePE pe : sourceStatement.getAdditivePEList())
            orClasses.add(resolveInferredValueClass(pe, inferred));
        return orClasses(orClasses);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFAdditivePE sourceStatement, @Nullable InferResult inferred) {
        List<LSFClassSet> orClasses = new ArrayList<LSFClassSet>();
        for (LSFMultiplicativePE pe : sourceStatement.getMultiplicativePEList())
            orClasses.add(resolveInferredValueClass(pe, inferred));
        return orClasses(orClasses);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFMultiplicativePE sourceStatement, @Nullable InferResult inferred) {
        List<LSFClassSet> orClasses = new ArrayList<LSFClassSet>();
        for (LSFUnaryMinusPE pe : sourceStatement.getUnaryMinusPEList())
            orClasses.add(resolveInferredValueClass(pe, inferred));
        return orClasses(orClasses);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFUnaryMinusPE sourceStatement, @Nullable InferResult inferred) {
        LSFUnaryMinusPE unaryMinusPE = sourceStatement.getUnaryMinusPE();
        if (unaryMinusPE != null)
            return resolveInferredValueClass(unaryMinusPE, inferred);

        return resolveInferredValueClass(sourceStatement.getPostfixUnaryPE(), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFPostfixUnaryPE sourceStatement, @Nullable InferResult inferred) {
        LSFClassSet valueClass = resolveInferredValueClass(sourceStatement.getSimplePE(), inferred);

        LSFUintLiteral uintLiteral = sourceStatement.getUintLiteral();
        if (uintLiteral != null) {
            if (valueClass instanceof StructClassSet)
                return ((StructClassSet) valueClass).get(Integer.valueOf(uintLiteral.getText()) - 1);
            return null;
        }

        return valueClass;
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFSimplePE sourceStatement, @Nullable InferResult inferred) {
        LSFPropertyExpression pe = sourceStatement.getPropertyExpression();
        if (pe != null)
            return pe.resolveInferredValueClass(inferred);

        return resolveInferredValueClass(sourceStatement.getExpressionPrimitive(), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFExpressionPrimitive sourceStatement, @Nullable InferResult inferred) {
        LSFExprParameterUsage ep = sourceStatement.getExprParameterUsage();
        if (ep != null)
            return resolveInferredValueClass(ep, inferred);

        return resolveInferredValueClass(sourceStatement.getExpressionFriendlyPD(), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFExprParameterUsage sourceStatement, @Nullable InferResult inferred) {
        LSFExprParameterNameUsage nameParam = sourceStatement.getExprParameterNameUsage();
        if (nameParam != null)
            return nameParam.resolveInferredClass(inferred);
        return null;
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFExpressionFriendlyPD sourceStatement, @Nullable InferResult inferred) {
        return ((FriendlyPE) sourceStatement.getChildren()[0]).resolveInferredValueClass(inferred);
    }

    private static LSFClassSet resolveValueClass(LSFPropertyUsage usage, boolean infer) {
        LSFPropDeclaration decl = usage.resolveDecl();
        if (decl != null)
            return decl.resolveValueClass(infer);
        return null;
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFJoinPropertyDefinition sourceStatement, @Nullable InferResult inferred) {
        LSFPropertyObject propertyObject = sourceStatement.getPropertyObject();
        LSFPropertyUsage usage = propertyObject.getPropertyUsage();
        if (usage != null) {
            LSFClassSet valueClass = resolveValueClass(usage, inferred != null);
            if (valueClass != null)
                return valueClass;
            return null;
        }

        LSFPropertyExprObject exprObject = propertyObject.getPropertyExprObject();
        LSFPropertyExpression pe = exprObject.getPropertyExpression();
        if (pe != null)
            return pe.resolveValueClass(inferred != null);
        return exprObject.getExpressionUnfriendlyPD().resolveUnfriendValueClass(inferred != null);
    }

    @Nullable
    private static LSFClassSet resolveInferredValueClass(List<LSFPropertyExpression> list, @Nullable InferResult inferred) {
        if (list.size() == 0)
            return null;

        LSFClassSet result = null;
        for (int i = 0; i < list.size(); i++) {
            LSFClassSet valueClass = list.get(i).resolveInferredValueClass(inferred);
            if (i == 0)
                result = valueClass;
            else
                result = or(result, valueClass);
        }
        return result;
    }

    private static LSFClassSet resolveInferredValueClass(LSFNonEmptyPropertyExpressionList list, @Nullable InferResult inferred) {
        return resolveInferredValueClass(list.getPropertyExpressionList(), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFMultiPropertyDefinition sourceStatement, @Nullable InferResult inferred) {
        return resolveInferredValueClass(sourceStatement.getNonEmptyPropertyExpressionList(), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFOverridePropertyDefinition sourceStatement, @Nullable InferResult inferred) {
        return resolveInferredValueClass(sourceStatement.getNonEmptyPropertyExpressionList(), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFIfElsePropertyDefinition sourceStatement, @Nullable InferResult inferred) {
        List<LSFPropertyExpression> list = sourceStatement.getPropertyExpressionList();
        return resolveInferredValueClass(list.subList(1, list.size()), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFMaxPropertyDefinition sourceStatement, @Nullable InferResult inferred) {
        return resolveInferredValueClass(sourceStatement.getNonEmptyPropertyExpressionList(), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFCasePropertyDefinition sourceStatement, @Nullable InferResult inferred) {
        List<LSFPropertyExpression> list = new ArrayList<LSFPropertyExpression>();
        for (LSFCaseBranchBody caseBranch : sourceStatement.getCaseBranchBodyList())
            list.add(caseBranch.getPropertyExpressionList().get(1));
        LSFPropertyExpression elseExpr = sourceStatement.getPropertyExpression();
        if (elseExpr != null)
            list.add(elseExpr);
        return resolveInferredValueClass(list, inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFPartitionPropertyDefinition sourceStatement, @Nullable InferResult inferred) {
        return sourceStatement.getPropertyExpression().resolveInferredValueClass(inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFRecursivePropertyDefinition sourceStatement, @Nullable InferResult inferred) {
        return resolveInferredValueClass(sourceStatement.getPropertyExpressionList(), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFStructCreationPropertyDefinition sourceStatement, @Nullable InferResult inferred) {
        List<LSFPropertyExpression> peList = sourceStatement.getNonEmptyPropertyExpressionList().getPropertyExpressionList();

        LSFClassSet[] sets = new LSFClassSet[peList.size()];
        for (int i = 0; i < sets.length; i++)
            sets[i] = resolveInferredValueClass(peList.get(i), inferred);
        return new StructClassSet(sets);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFCastPropertyDefinition sourceStatement, @Nullable InferResult inferred) {
        return resolve(sourceStatement.getBuiltInClassName());
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFConcatPropertyDefinition sourceStatement, @Nullable InferResult inferred) {
        return resolveInferredValueClass(sourceStatement.getNonEmptyPropertyExpressionList(), inferred);
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFSessionPropertyDefinition sourceStatement, @Nullable InferResult inferred) {
        LSFSessionPropertyType type = sourceStatement.getSessionPropertyType();
        if (type.getText().equals("PREV"))
            return resolveInferredValueClass(sourceStatement.getPropertyExpression(), inferred);
        return DataClass.BOOLEAN;
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFSignaturePropertyDefinition sourceStatement, @Nullable InferResult inferred) {
        return DataClass.BOOLEAN;
    }

    @Nullable
    public static LSFClassSet resolveInferredValueClass(@NotNull LSFLiteral sourceStatement, @Nullable InferResult inferred) {
        if (sourceStatement.getBooleanLiteral() != null)
            return DataClass.BOOLEAN;
        if (sourceStatement.getUlongLiteral() != null)
            return new LongClass();
        if (sourceStatement.getUintLiteral() != null)
            return new IntegerClass();
        if (sourceStatement.getUdoubleLiteral() != null)
            return new DoubleClass();
        if (sourceStatement.getUnumericLiteral() != null) {
            String name = sourceStatement.getText();
            String whole = name.substring(0, name.indexOf("."));
            String precision = name.substring(name.indexOf(".") + 1, name.length() - 1);
            return new NumericClass(whole.length() + precision.length(), precision.length());
        }
        LSFStringLiteral stringLiteral = sourceStatement.getStringLiteral();
        if (stringLiteral != null)
            return new StringClass(false, true, new ExtInt(stringLiteral.getText().length()));
        LSFDateTimeLiteral dateTimeLiteral = sourceStatement.getDateTimeLiteral();
        if (dateTimeLiteral != null)
            return new SimpleDataClass("DATETIME");
        LSFDateLiteral dateLiteral = sourceStatement.getDateLiteral();
        if (dateLiteral != null)
            return new SimpleDataClass("DATE");
        LSFTimeLiteral timeLiteral = sourceStatement.getTimeLiteral();
        if (timeLiteral != null)
            return new SimpleDataClass("TIME");
        LSFColorLiteral colorLiteral = sourceStatement.getColorLiteral();
        if (colorLiteral != null)
            return new SimpleDataClass("COLOR");
        LSFStaticObjectID staticObject = sourceStatement.getStaticObjectID();
        if (staticObject != null)
            return resolveClass(staticObject.getCustomClassUsage());
        return null;
    }

    // UnfriendlyPE.resolveValueClass

    @Nullable
    public static LSFClassSet resolveUnfriendValueClass(@NotNull LSFExpressionUnfriendlyPD sourceStatement, boolean infer) {
        LSFContextIndependentPD contextIndependentPD = sourceStatement.getContextIndependentPD();
        if (contextIndependentPD != null)
            return ((UnfriendlyPE) contextIndependentPD.getChildren()[0]).resolveUnfriendValueClass(infer);
        return null;
    }

    @Nullable
    public static LSFClassSet resolveUnfriendValueClass(@NotNull LSFDataPropertyDefinition sourceStatement, boolean infer) {
        return resolveClass(sourceStatement.getClassName());
    }

    @Nullable
    public static LSFClassSet resolveUnfriendValueClass(@NotNull LSFNativePropertyDefinition sourceStatement, boolean infer) {
        return resolveClass(sourceStatement.getClassName());
    }

    @Nullable
    public static LSFClassSet resolveUnfriendValueClass(@NotNull LSFAbstractPropertyDefinition sourceStatement, boolean infer) {
        return resolveClass(sourceStatement.getClassName());
    }

    @Nullable
    public static LSFClassSet resolveUnfriendValueClass(@NotNull LSFAbstractActionPropertyDefinition sourceStatement, boolean infer) {
        return null;
    }

    private static InferResult inferGroupParamClasses(LSFGroupPropertyDefinition sourceStatement) {
        Inferred result = Inferred.EMPTY;
        for (LSFPropertyExpression expr : getContextExprs(sourceStatement))
            result = result.and(inferParamClasses(expr, null));
        return result.finish();
    }

    @Nullable
    public static LSFClassSet resolveUnfriendValueClass(@NotNull LSFGroupPropertyDefinition sourceStatement, boolean infer) {
        return resolveInferredValueClass(sourceStatement.getNonEmptyPropertyExpressionList(), infer ? inferGroupParamClasses(sourceStatement) : null);
    }

    @Nullable
    public static LSFClassSet resolveUnfriendValueClass(@NotNull LSFFormulaPropertyDefinition sourceStatement, boolean infer) {
        LSFBuiltInClassName builtIn = sourceStatement.getBuiltInClassName();
        if (builtIn != null)
            return resolve(builtIn);
        return null;
    }

    @Nullable
    public static LSFClassSet resolveUnfriendValueClass(@NotNull LSFFilterPropertyDefinition sourceStatement, boolean infer) {
        return DataClass.BOOLEAN;
    }

    // UnfriendlyPE.resolveValueParamClasses

    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFEditFormActionPropertyDefinitionBody editForm) {
        return Collections.singletonList(resolveClass(editForm.getCustomClassUsage()));
    }

    // извращенная эмуляция unfriendly
    public static List<LSFClassSet> resolveActionUnfriendlyValueParamClasses(@NotNull LSFActionPropertyDefinition actionDef) {
        // извращенная эмуляция unfriendly
        LSFActionPropertyDefinitionBody body = actionDef.getActionPropertyDefinitionBody();
        LSFAddFormActionPropertyDefinitionBody addForm = body.getAddFormActionPropertyDefinitionBody();
        if (addForm != null)
            return new ArrayList<LSFClassSet>();
        LSFEditFormActionPropertyDefinitionBody editForm = body.getEditFormActionPropertyDefinitionBody();
        if (editForm != null)
            return resolveValueParamClasses(editForm);
        LSFCustomActionPropertyDefinitionBody custom = body.getCustomActionPropertyDefinitionBody();
        if (custom != null) {
            LSFClassNameList classNameList = custom.getClassNameList();
            if (classNameList != null)
                return resolveClass(classNameList);
        }
        return null;
    }

    @Nullable
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFExpressionUnfriendlyPD sourceStatement) {
        LSFContextIndependentPD contextIndependentPD = sourceStatement.getContextIndependentPD();
        if (contextIndependentPD != null)
            return ((UnfriendlyPE) contextIndependentPD.getChildren()[0]).resolveValueParamClasses();

        LSFActionPropertyDefinition actionDef = sourceStatement.getActionPropertyDefinition();
        LSFExprParameterUsageList actionParams = actionDef.getExprParameterUsageList();
        if (actionParams != null) {
            List<LSFExprParamReference> paramRefs = new ArrayList<LSFExprParamReference>();
            for (LSFExprParameterUsage actionParam : actionParams.getExprParameterUsageList())
                paramRefs.add(actionParam.getExprParameterNameUsage());
            return resolveParamRefClasses(paramRefs);
        }

        return resolveActionUnfriendlyValueParamClasses(actionDef);
    }

    @Nullable
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFDataPropertyDefinition sourceStatement) {
        return resolveClass(sourceStatement.getClassNameList());
    }

    @Nullable
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFNativePropertyDefinition sourceStatement) {
        return resolveClass(sourceStatement.getClassNameList());
    }

    @Nullable
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFAbstractPropertyDefinition sourceStatement) {
        return resolveClass(sourceStatement.getClassNameList());
    }

    @Nullable
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFAbstractActionPropertyDefinition sourceStatement) {
        return resolveClass(sourceStatement.getClassNameList());
    }

    @Nullable
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        LSFGroupPropertyBy groupBy = sourceStatement.getGroupPropertyBy();
        if (groupBy == null)
            return new ArrayList<LSFClassSet>();

        return resolveParamClasses(groupBy.getNonEmptyPropertyExpressionList());
    }

    @Nullable
    public static List<LSFClassSet> inferValueParamClasses(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        LSFGroupPropertyBy groupBy = sourceStatement.getGroupPropertyBy();
        if (groupBy == null)
            return new ArrayList<LSFClassSet>();

        return resolveParamClasses(groupBy.getNonEmptyPropertyExpressionList(), inferGroupParamClasses(sourceStatement));
    }

    @Nullable
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFFormulaPropertyDefinition sourceStatement) {
        String text = sourceStatement.getStringLiteral().getText();
        int i = 0;
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();
        while (text.contains("$" + (i + 1))) {
            i++;
            result.add(null);
        }
        return result;
    }

    @Nullable
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFFilterPropertyDefinition sourceStatement) {
        List<LSFClassSet> classes = sourceStatement.getGroupObjectID().getGroupObjectUsage().resolveClasses();
        if (classes != null)
            return classes;
        return null;
    }

    // LSFPropertyExpression.resolveValueParamClasses
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFPropertyExpression sourceStatement) {
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();
        for (LSFExprParamDeclaration param : sourceStatement.resolveParams())
            result.add(param.resolveClass());
        return result;
    }

    @NotNull
    public static List<LSFExprParamDeclaration> resolveParams(@NotNull LSFPropertyExpression sourceStatement) {
        return new ExprsContextModifier(sourceStatement).resolveParams(Integer.MAX_VALUE, new HashSet<LSFExprParamDeclaration>());
    }

    public static List<LSFParamDeclaration> resolveParams(LSFClassParamDeclareList classNameList) {
        if (classNameList == null)
            return null;

        List<LSFParamDeclaration> result = new ArrayList<LSFParamDeclaration>();

        LSFNonEmptyClassParamDeclareList ne = classNameList.getNonEmptyClassParamDeclareList();
        if (ne == null)
            return result;
        for (LSFClassParamDeclare classParamDeclare : ne.getClassParamDeclareList())
            result.add(classParamDeclare.getParamDeclare());
        return result;
    }

    public static List<LSFParamDeclaration> resolveParams(LSFExprParameterUsageList classNameList) {
        if (classNameList == null)
            return null;

        List<LSFParamDeclaration> result = new ArrayList<LSFParamDeclaration>();

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
        if (propertyDeclaration.getClassParamDeclareList() != null) {
            LSFNonEmptyClassParamDeclareList paramDeclareList = propertyDeclaration.getClassParamDeclareList().getNonEmptyClassParamDeclareList();
            if (paramDeclareList != null) {
                result = new ArrayList<LSFParamDeclaration>();
                for (LSFClassParamDeclare paramDeclare : paramDeclareList.getClassParamDeclareList())
                    result.add(paramDeclare.getParamDeclare());
            }
        }
        return result;
    }

    // PROPERTYUSAGECONTEXT.RESOLVEPARAMCLASSES

    public static List<LSFClassSet> resolveParamDeclClasses(List<? extends LSFExprParamDeclaration> decls) {
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();
        for (LSFExprParamDeclaration decl : decls)
            result.add(decl.resolveClass());
        return result;
    }

    public static List<LSFClassSet> resolveParamRefClasses(List<? extends LSFAbstractParamReference> refs) {
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();
        for (LSFAbstractParamReference ref : refs)
            result.add(ref.resolveClass());
        return result;
    }

    public static List<LSFClassSet> resolveExprClasses(List<LSFPropertyExpression> exprs, @Nullable InferResult inferred) {
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();
        for (LSFPropertyExpression expr : exprs)
            result.add(expr.resolveInferredValueClass(inferred));
        return result;
    }

    public static List<LSFClassSet> resolveParamExprClasses(List<LSFExprParameterUsage> exprs) {
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();
        for (LSFExprParameterUsage expr : exprs)
            result.add(resolveInferredValueClass(expr, null));
        return result;
    }

    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFClassParamDeclareList sourceStatement) {
        LSFNonEmptyClassParamDeclareList ne = sourceStatement.getNonEmptyClassParamDeclareList();
        if (ne != null) {
            List<LSFClassSet> result = new ArrayList<LSFClassSet>();
            for (LSFClassParamDeclare decl : ne.getClassParamDeclareList())
                result.add(decl.resolveClass());
            return result;
        }

        return new ArrayList<LSFClassSet>();
    }

    public static List<LSFObjectUsage> getObjectUsageList(LSFObjectUsageList objectUsageList) {
        LSFNonEmptyObjectUsageList neList = objectUsageList.getNonEmptyObjectUsageList();
        if (neList != null)
            return neList.getObjectUsageList();
        return new ArrayList<LSFObjectUsage>();
    }

    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFObjectUsageList sourceStatement) {
        return resolveParamRefClasses(getObjectUsageList(sourceStatement));
    }

    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFNonEmptyPropertyExpressionList sourceStatement, @Nullable InferResult inferred) {
        return resolveExprClasses(sourceStatement.getPropertyExpressionList(), inferred);
    }

    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFNonEmptyPropertyExpressionList sourceStatement) {
        return resolveExprClasses(sourceStatement.getPropertyExpressionList(), null);
    }

    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFPropertyExpressionList sourceStatement) {
        LSFNonEmptyPropertyExpressionList nonEmpty = sourceStatement.getNonEmptyPropertyExpressionList();
        if (nonEmpty != null)
            return resolveParamClasses(nonEmpty);
        return new ArrayList<LSFClassSet>();
    }

    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFExprParameterUsageList sourceStatement) {
        return resolveParamExprClasses(sourceStatement.getExprParameterUsageList());
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFJoinPropertyDefinition sourceStatement) {
        return resolveParamClasses(sourceStatement.getPropertyExpressionList());
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFExecActionPropertyDefinitionBody sourceStatement) {
        return resolveParamClasses(sourceStatement.getPropertyExpressionList());
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFNoContextPropertyUsage sourceStatement) {
        return null;
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFPartitionPropertyDefinition sourceStatement) {
        LSFPartitionPropertyBy by = sourceStatement.getPartitionPropertyBy();
        if (by != null)
            return resolveParamClasses(by.getNonEmptyPropertyExpressionList());
        return new ArrayList<LSFClassSet>();
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
    public static PsiElement getParamList(@NotNull LSFExecActionPropertyDefinitionBody sourceStatement) {
        return sourceStatement.getPropertyExpressionList();
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFFormMappedProperty sourceStatement) {
        return sourceStatement.getObjectUsageList();
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
    public static PsiElement getParamList(@NotNull LSFMappedPropertyObject sourceStatement) {
        return sourceStatement.getObjectUsageList();
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFMappedPropertyClassParamDeclare sourceStatement) {
        return sourceStatement.getClassParamDeclareList();
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFMappedPropertyExprParam sourceStatement) {
        return sourceStatement.getExprParameterUsageList();
    }


    private static List<LSFFormObjectDeclaration> getObjectDecls(LSFFormCommonGroupObject commonGroup) {
        LSFFormSingleGroupObjectDeclaration singleGroup = commonGroup.getFormSingleGroupObjectDeclaration();
        if (singleGroup != null)
            return Collections.singletonList(singleGroup.getFormObjectDeclaration());
        return commonGroup.getFormMultiGroupObjectDeclaration().getFormObjectDeclarationList();
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFFormTreeGroupObjectDeclaration sourceStatement) {
        return resolveParamDeclClasses(getObjectDecls(sourceStatement.getFormCommonGroupObject()));
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFFormMappedProperty sourceStatement) {
        return resolveParamClasses(sourceStatement.getObjectUsageList());
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFFormMappedNamePropertiesList sourceStatement) {
        return resolveParamClasses(sourceStatement.getObjectUsageList());
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFMappedPropertyObject sourceStatement) {
        return resolveParamClasses(sourceStatement.getObjectUsageList());
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFMappedPropertyClassParamDeclare sourceStatement) {
        return resolveParamClasses(sourceStatement.getClassParamDeclareList());
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFMappedPropertyExprParam sourceStatement) {
        return resolveParamClasses(sourceStatement.getExprParameterUsageList());
    }

    // PROPERTYEXPRESSION.INFERPARAMCLASSES

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFPropertyExpression sourceStatement, @Nullable LSFClassSet valueClass) {
        return inferParamClasses(sourceStatement.getIfPE(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFIfPE sourceStatement, @Nullable LSFClassSet valueClass) {
        List<LSFOrPE> peList = sourceStatement.getOrPEList();

        Inferred result = inferParamClasses(peList.get(0), valueClass);
        for (int i = 1; i < peList.size(); i++)
            result = result.and(inferParamClasses(peList.get(i), null)); // подойдут все классы, так как boolean
        return result;
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFOrPE sourceStatement, @Nullable LSFClassSet valueClass) {
        List<LSFXorPE> peList = sourceStatement.getXorPEList();
        if (peList.size() == 1)
            return inferParamClasses(peList.get(0), valueClass);

        List<Inferred> maps = new ArrayList<Inferred>();
        for (LSFXorPE xorPe : peList)
            maps.add(inferParamClasses(xorPe, null)); // подойдут все классы, так как boolean
        return Inferred.orClasses(maps);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFXorPE sourceStatement, @Nullable LSFClassSet valueClass) {
        List<LSFAndPE> peList = sourceStatement.getAndPEList();
        if (peList.size() == 1)
            return inferParamClasses(peList.get(0), valueClass);

        List<Inferred> maps = new ArrayList<Inferred>();
        for (LSFAndPE andPe : peList)
            maps.add(inferParamClasses(andPe, null)); // подойдут все классы, так как boolean
        return Inferred.orClasses(maps);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFAndPE sourceStatement, @Nullable LSFClassSet valueClass) {
        List<LSFNotPE> peList = sourceStatement.getNotPEList();
        if (peList.size() == 1)
            return inferParamClasses(peList.get(0), valueClass);

        List<Inferred> maps = new ArrayList<Inferred>();
        for (LSFNotPE notPE : peList)
            maps.add(inferParamClasses(notPE, null)); // подойдут все классы, так как boolean
        return Inferred.andClasses(maps);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFNotPE sourceStatement, @Nullable LSFClassSet valueClass) {
        LSFEqualityPE eqPE = sourceStatement.getEqualityPE();
        if (eqPE != null)
            return inferParamClasses(eqPE, valueClass);
        return new Inferred(inferParamClasses(sourceStatement.getNotPE(), null), true);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFEqualityPE sourceStatement, @Nullable LSFClassSet valueClass) {
        List<LSFRelationalPE> list = sourceStatement.getRelationalPEList();
        if (list.size() == 2)
            return new Inferred(new Equals(list.get(0), list.get(1)));
        return inferParamClasses(list.get(0), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFRelationalPE sourceStatement, @Nullable LSFClassSet valueClass) {
        List<LSFAdditiveORPE> list = sourceStatement.getAdditiveORPEList();

        LSFTypePropertyDefinition typeDef = null;
        if ((typeDef = sourceStatement.getTypePropertyDefinition()) != null)
            return inferParamClasses(list.get(0), resolveClass(typeDef.getClassName()));

        if (list.size() == 2)
            return new Inferred(new Relationed(list.get(0), list.get(1)));
        return inferParamClasses(list.get(0), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFAdditiveORPE sourceStatement, @Nullable LSFClassSet valueClass) {
        List<Inferred> maps = new ArrayList<Inferred>();
        for (LSFAdditivePE additivePE : sourceStatement.getAdditivePEList())
            maps.add(inferParamClasses(additivePE, valueClass));
        return Inferred.orClasses(maps);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFAdditivePE sourceStatement, @Nullable LSFClassSet valueClass) {
        List<Inferred> maps = new ArrayList<Inferred>();
        for (LSFMultiplicativePE additivePE : sourceStatement.getMultiplicativePEList())
            maps.add(inferParamClasses(additivePE, valueClass));
        return Inferred.andClasses(maps);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFMultiplicativePE sourceStatement, @Nullable LSFClassSet valueClass) {
        List<Inferred> maps = new ArrayList<Inferred>();
        for (LSFUnaryMinusPE additivePE : sourceStatement.getUnaryMinusPEList())
            maps.add(inferParamClasses(additivePE, valueClass));
        return Inferred.andClasses(maps);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFUnaryMinusPE sourceStatement, @Nullable LSFClassSet valueClass) {
        LSFUnaryMinusPE unaryMinusPE = sourceStatement.getUnaryMinusPE();
        if (unaryMinusPE != null)
            return inferParamClasses(unaryMinusPE, valueClass);

        return inferParamClasses(sourceStatement.getPostfixUnaryPE(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFPostfixUnaryPE sourceStatement, @Nullable LSFClassSet valueClass) {
        LSFUintLiteral uintLiteral = sourceStatement.getUintLiteral();
        if (uintLiteral != null) {
            if (valueClass instanceof StructClassSet)
                valueClass = ((StructClassSet) valueClass).get(Integer.valueOf(uintLiteral.getText()) - 1);
            else
                valueClass = null;
        }

        return inferParamClasses(sourceStatement.getSimplePE(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFSimplePE sourceStatement, @Nullable LSFClassSet valueClass) {
        LSFPropertyExpression pe = sourceStatement.getPropertyExpression();
        if (pe != null)
            return inferParamClasses(pe, valueClass);

        return inferParamClasses(sourceStatement.getExpressionPrimitive(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFExpressionPrimitive sourceStatement, @Nullable LSFClassSet valueClass) {
        LSFExprParameterUsage ep = sourceStatement.getExprParameterUsage();
        if (ep != null)
            return inferParamClasses(ep, valueClass);

        return inferParamClasses(sourceStatement.getExpressionFriendlyPD(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFExprParameterUsage sourceStatement, @Nullable LSFClassSet valueClass) {
        LSFExprParameterNameUsage nameParam = sourceStatement.getExprParameterNameUsage();
        if (nameParam != null) {
            LSFExprParamDeclaration decl = nameParam.resolveDecl();

            if (decl != null) {
                LSFClassSet declClass = decl.resolveClass();
                if (declClass == null)
                    declClass = valueClass;
                return new Inferred(decl, declClass);
            }
        }
        return Inferred.EMPTY;
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFExpressionFriendlyPD sourceStatement, @Nullable LSFClassSet valueClass) {
        return ((FriendlyPE) sourceStatement.getChildren()[0]).inferParamClasses(valueClass);
    }

    public static Inferred inferJoinParamClasses(@NotNull LSFPropertyObject propertyObject, LSFPropertyExpressionList peList, @Nullable LSFClassSet valueClass) {
        List<LSFClassSet> joinClasses = inferParamClasses(valueClass, propertyObject);

        List<LSFPropertyExpression> list;
        LSFNonEmptyPropertyExpressionList npeList = peList.getNonEmptyPropertyExpressionList();
        if (npeList != null)
            list = npeList.getPropertyExpressionList();
        else
            list = new ArrayList<LSFPropertyExpression>();

        List<Inferred> classes = new ArrayList<Inferred>();
        for (int i = 0; i < list.size(); i++)
            classes.add(inferParamClasses(list.get(i), joinClasses != null && i < joinClasses.size() ? joinClasses.get(i) : null));
        return Inferred.andClasses(classes);

    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFJoinPropertyDefinition sourceStatement, @Nullable LSFClassSet valueClass) {
        return inferJoinParamClasses(sourceStatement.getPropertyObject(), sourceStatement.getPropertyExpressionList(), valueClass);
    }

    private static List<LSFClassSet> inferParamClasses(LSFClassSet valueClass, LSFPropertyUsage usage) {
        LSFPropDeclaration decl = usage.resolveDecl();
        if (decl != null)
            return decl.inferParamClasses(valueClass);
        return null;
    }

    private static List<LSFClassSet> inferParamClasses(LSFClassSet valueClass, LSFPropertyObject propertyObject) {
        LSFPropertyUsage usage = propertyObject.getPropertyUsage();
        List<LSFClassSet> joinClasses;
        if (usage != null) {
            joinClasses = inferParamClasses(valueClass, usage);
        } else {
            LSFPropertyExprObject exprObject = propertyObject.getPropertyExprObject();
            LSFPropertyExpression pe = exprObject.getPropertyExpression();
            if (pe != null) {
                List<LSFExprParamDeclaration> params = pe.resolveParams();
                joinClasses = new ArrayList<LSFClassSet>();
                InferResult inferred = inferParamClasses(pe, valueClass).finish();
                for (LSFExprParamDeclaration param : params)
                    joinClasses.add(inferred.get(param));
            } else {
                LSFExpressionUnfriendlyPD unfriendlyPD = exprObject.getExpressionUnfriendlyPD();
                LSFContextIndependentPD contextIndependentPD = unfriendlyPD.getContextIndependentPD();
                if (contextIndependentPD != null) { // кое где есть ACTION'ы в propertyObject
                    PsiElement element = contextIndependentPD.getChildren()[0]; // лень создавать отдельный параметр или интерфейс
                    if (element instanceof LSFGroupPropertyDefinition) {
                        joinClasses = LSFPsiImplUtil.inferValueParamClasses((LSFGroupPropertyDefinition) element);
                    } else
                        joinClasses = unfriendlyPD.resolveValueParamClasses();
                } else {
                    LSFActionPropertyDefinition actionDef = unfriendlyPD.getActionPropertyDefinition();
                    LSFExprParameterUsageList params = actionDef.getExprParameterUsageList();
                    if (params != null) { // и есть declaration'ы
                        List<LSFExprParamDeclaration> paramDecls = new ArrayList<LSFExprParamDeclaration>();
                        for (LSFExprParameterUsage param : params.getExprParameterUsageList())
                            paramDecls.add(param.getExprParameterNameUsage().getClassParamDeclare().getParamDeclare());
                        InferResult inferred = inferActionParamClasses(actionDef.getActionPropertyDefinitionBody(), new HashSet<LSFExprParamDeclaration>(paramDecls)).finish();
                        joinClasses = new ArrayList<LSFClassSet>();
                        for (LSFExprParamDeclaration param : paramDecls)
                            joinClasses.add(inferred.get(param));
                    } else { // contextUnfriendly
                        joinClasses = resolveActionUnfriendlyValueParamClasses(actionDef);
                    }
                }
            }
        }
        return joinClasses;
    }

    @NotNull
    private static Inferred inferParamClasses(List<LSFPropertyExpression> list, @Nullable LSFClassSet valueClass) {
        if (list.size() == 0)
            return Inferred.EMPTY;

        List<Inferred> classes = new ArrayList<Inferred>();
        for (LSFPropertyExpression expr : list)
            classes.add(inferParamClasses(expr, valueClass));
        return Inferred.orClasses(classes);
    }

    private static Inferred inferParamClasses(LSFNonEmptyPropertyExpressionList list, @Nullable LSFClassSet valueClass) {
        return inferParamClasses(list.getPropertyExpressionList(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFMultiPropertyDefinition sourceStatement, @Nullable LSFClassSet valueClass) {
        return inferParamClasses(sourceStatement.getNonEmptyPropertyExpressionList(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFOverridePropertyDefinition sourceStatement, @Nullable LSFClassSet valueClass) {
        return inferParamClasses(sourceStatement.getNonEmptyPropertyExpressionList(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFIfElsePropertyDefinition sourceStatement, @Nullable LSFClassSet valueClass) {
        List<LSFPropertyExpression> list = sourceStatement.getPropertyExpressionList();
        Inferred result = inferParamClasses(list.get(0), null).and(inferParamClasses(list.get(1), valueClass));
        if (list.size() == 3)
            result = result.or(inferParamClasses(list.get(2), valueClass));
        return result;
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFMaxPropertyDefinition sourceStatement, @Nullable LSFClassSet valueClass) {
        return inferParamClasses(sourceStatement.getNonEmptyPropertyExpressionList(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFCasePropertyDefinition sourceStatement, @Nullable LSFClassSet valueClass) {
        List<Inferred> list = new ArrayList<Inferred>();
        for (LSFCaseBranchBody caseBranch : sourceStatement.getCaseBranchBodyList()) {
            List<LSFPropertyExpression> peList = caseBranch.getPropertyExpressionList();
            list.add(inferParamClasses(peList.get(0), null).and(inferParamClasses(peList.get(1), valueClass)));
        }
        LSFPropertyExpression elseExpr = sourceStatement.getPropertyExpression();
        if (elseExpr != null)
            list.add(inferParamClasses(elseExpr, valueClass));
        return Inferred.orClasses(list);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFPartitionPropertyDefinition sourceStatement, @Nullable LSFClassSet valueClass) {
        List<Inferred> classes = new ArrayList<Inferred>();
        classes.add(inferParamClasses(sourceStatement.getPropertyExpression(), valueClass));

        LSFPartitionPropertyBy partitionBy = sourceStatement.getPartitionPropertyBy();
        if (partitionBy != null) {
            List<LSFClassSet> joinClasses = null;
            LSFPropertyObject po = sourceStatement.getPropertyObject();
            if (po != null)
                joinClasses = inferParamClasses(valueClass, po);

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
    public static Inferred inferParamClasses(@NotNull LSFRecursivePropertyDefinition sourceStatement, @Nullable LSFClassSet valueClass) {
        List<LSFPropertyExpression> peList = sourceStatement.getPropertyExpressionList();
        return inferParamClasses(peList.get(0), valueClass).or(inferParamClasses(peList.get(1), valueClass));
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFStructCreationPropertyDefinition sourceStatement, @Nullable LSFClassSet valueClass) {
        List<Inferred> classes = new ArrayList<Inferred>();
        for (LSFPropertyExpression pe : sourceStatement.getNonEmptyPropertyExpressionList().getPropertyExpressionList())
            classes.add(inferParamClasses(pe, null));
        return Inferred.andClasses(classes);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFCastPropertyDefinition sourceStatement, @Nullable LSFClassSet valueClass) {
        return inferParamClasses(sourceStatement.getPropertyExpression(), null); // из-за cast'а
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFConcatPropertyDefinition sourceStatement, @Nullable LSFClassSet valueClass) {
        return inferParamClasses(sourceStatement.getNonEmptyPropertyExpressionList(), valueClass);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFSessionPropertyDefinition sourceStatement, @Nullable LSFClassSet valueClass) {
        return inferParamClasses(sourceStatement.getPropertyExpression(), null);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFSignaturePropertyDefinition sourceStatement, @Nullable LSFClassSet valueClass) {
        return inferParamClasses(sourceStatement.getPropertyExpression(), null);
    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFLiteral sourceStatement, @Nullable LSFClassSet valueClass) {
        return Inferred.EMPTY;
    }

    public static void ensureClass(@NotNull LSFClassParamDeclare sourceStatement, @NotNull LSFValueClass valueClass, MetaTransaction metaTrans) {
        LSFClassName className = sourceStatement.getClassName();
        if (className == null) {
            LeafElement whitespace = ASTFactory.whitespace(" ");
            LSFClassName genName = LSFElementGenerator.createClassNameFromText(sourceStatement.getProject(), valueClass.getQName(sourceStatement));
            ASTNode paramDeclare = sourceStatement.getParamDeclare().getNode();

            if (metaTrans != null) // !! важно до делать
                metaTrans.regAddChange(BaseUtils.toList(genName.getNode(), whitespace), paramDeclare);

            ASTNode sourceNode = sourceStatement.getNode();
            sourceNode.addChild(whitespace, paramDeclare);
            sourceNode.addChild(genName.getNode(), whitespace);
        }
    }

    /////
    public static void ensureClass(@NotNull LSFForAddObjClause sourceStatement, @NotNull LSFValueClass valueClass, MetaTransaction metaTrans) {
    }


    public static Inferred inferActionParamClasses(LSFActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return ((ActionExpression) body.getChildren()[0]).inferActionParamClasses(params);
    }

    public static Inferred inferActionParamClasses(LSFAssignActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        List<LSFPropertyExpression> peList = body.getPropertyExpressionList();
        LSFPropertyExpression peValue = peList.get(0);
        LSFMappedPropertyExprParam peTo = body.getMappedPropertyExprParam();

        Inferred result = inferParamClasses(peTo, peValue.resolveValueClass(true)).filter(params);
        //orClasses(BaseUtils.filterNullable(inferParamClasses(peValue, resolveValueClass(peTo.getPropertyUsage(), true)), params)
        if (peList.size() == 2)
            result = result.and(inferParamClasses(peList.get(1), null).filter(params));
        return inferParamClasses(peValue, resolveValueClass(peTo.getPropertyUsage(), true)).override(result);
    }

    public static Inferred inferActionParamClasses(LSFForActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а 
        LSFPropertyExpression expr = body.getPropertyExpression();
        Inferred forClasses = Inferred.EMPTY;
        if (expr != null)
            forClasses = forClasses.and(inferParamClasses(expr, null).filter(params));
        LSFNonEmptyPropertyExpressionList npeList = body.getNonEmptyPropertyExpressionList();
        if (npeList != null) {
            for (LSFPropertyExpression pe : npeList.getPropertyExpressionList())
                forClasses = forClasses.and(inferParamClasses(pe, null).filter(params));
        }

        List<LSFActionPropertyDefinitionBody> actions = body.getActionPropertyDefinitionBodyList();
        Inferred result = inferActionParamClasses(actions.get(0), params).override(forClasses);
        if (actions.size() == 2)
            result = result.or(inferActionParamClasses(actions.get(1), params));
        return result;
    }

    public static Inferred inferActionParamClasses(LSFWhileActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а 
        Inferred forClasses = inferParamClasses(body.getPropertyExpression(), null).filter(params);
        LSFNonEmptyPropertyExpressionList npeList = body.getNonEmptyPropertyExpressionList();
        if (npeList != null) {
            for (LSFPropertyExpression pe : npeList.getPropertyExpressionList())
                forClasses = forClasses.and(inferParamClasses(pe, null).filter(params));
        }

        return inferActionParamClasses(body.getActionPropertyDefinitionBody(), params).override(forClasses);
    }

    public static Inferred inferActionParamClasses(LSFChangeClassActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а
        LSFPropertyExpression expr = body.getPropertyExpression();
        if (expr != null)
            return inferParamClasses(expr, null).filter(params);
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFDeleteActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а
        LSFPropertyExpression expr = body.getPropertyExpression();
        if (expr != null)
            return inferParamClasses(expr, null).filter(params);
        return Inferred.EMPTY;
    }

    private static Inferred inferParamClasses(LSFMappedPropertyExprParam mappedProperty, @Nullable LSFClassSet valueClass) {
        List<LSFClassSet> joinClasses = inferParamClasses(null, mappedProperty.getPropertyUsage());

        List<LSFExprParameterUsage> list = mappedProperty.getExprParameterUsageList().getExprParameterUsageList();
        List<Inferred> classes = new ArrayList<Inferred>();
        for (int i = 0; i < list.size(); i++)
            classes.add(inferParamClasses(list.get(i), joinClasses != null && i < joinClasses.size() ? joinClasses.get(i) : null));
        return Inferred.andClasses(classes);
    }

    public static Inferred inferActionParamClasses(LSFAddObjectActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а
        Inferred result = Inferred.EMPTY;

        LSFPropertyExpression expr = body.getPropertyExpression();
        if (expr != null)
            result = inferParamClasses(expr, null).filter(params);

        LSFMappedPropertyExprParam to = body.getMappedPropertyExprParam();
        if (to != null)
            result = inferParamClasses(to, resolveClass(body.getCustomClassUsage())).filter(params).override(result);
        return result;
    }

    public static Inferred inferActionParamClasses(LSFListActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а
        Inferred result = Inferred.EMPTY;
        for (LSFActionPropertyDefinitionBody action : body.getActionPropertyDefinitionBodyList())
            result = result.or(inferActionParamClasses(action, params));
        return result;
    }

    public static Inferred inferActionParamClasses(LSFRequestInputActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        LSFActionPropertyDefinitionBody actionBody = body.getActionPropertyDefinitionBody();
        if (actionBody != null)
            return inferActionParamClasses(actionBody, params);
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFExecActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return inferJoinParamClasses(body.getPropertyObject(), body.getPropertyExpressionList(), null).filter(params);
    }

    public static Inferred inferActionParamClasses(LSFIfActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а 
        Inferred forClasses = inferParamClasses(body.getPropertyExpression(), null).filter(params);

        List<LSFActionPropertyDefinitionBody> actions = body.getActionPropertyDefinitionBodyList();
        Inferred result = inferActionParamClasses(actions.get(0), params).override(forClasses);
        if (actions.size() == 2)
            result = result.or(inferActionParamClasses(actions.get(1), params));
        return result;
    }

    public static Inferred inferActionParamClasses(LSFCaseActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а 
        List<Inferred> list = new ArrayList<Inferred>();
        for (LSFActionCaseBranchBody caseBranch : body.getActionCaseBranchBodyList())
            list.add(inferActionParamClasses(caseBranch.getActionPropertyDefinitionBody(), params).override(inferParamClasses(caseBranch.getPropertyExpression(), null).filter(params)));

        LSFActionPropertyDefinitionBody elseAction = body.getActionPropertyDefinitionBody();
        if (elseAction != null)
            list.add(inferActionParamClasses(elseAction, params));
        return Inferred.orClasses(list);
    }

    public static Inferred inferActionParamClasses(LSFMultiActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        List<Inferred> list = new ArrayList<Inferred>();
        for (LSFActionPropertyDefinitionBody action : body.getNonEmptyActionPDBList().getActionPropertyDefinitionBodyList())
            list.add(inferActionParamClasses(action, params));
        return Inferred.orClasses(list);
    }

    public static Inferred inferActionParamClasses(LSFTerminalFlowActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFFormActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        List<Inferred> list = new ArrayList<Inferred>();

        List<LSFFormActionObjectUsage> objectMap = new ArrayList<LSFFormActionObjectUsage>();
        LSFFormActionObjectList objects = body.getFormActionObjectList();
        if (objects != null)
            objectMap.addAll(objects.getFormActionObjectUsageList());

        LSFFormActionObjectUsage contextFilter = body.getFormActionObjectUsage();
        if (contextFilter != null)
            objectMap.add(contextFilter);

        for (LSFFormActionObjectUsage ou : objectMap)
            list.add(inferParamClasses(ou.getPropertyExpression(), ou.getObjectUsage().resolveClass()).filter(params));
        return Inferred.orClasses(list);
    }

    public static Inferred inferActionParamClasses(LSFAddFormActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFEditFormActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFCustomActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFMessageActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return inferParamClasses(body.getPropertyExpression(), null).filter(params);
    }

    public static Inferred inferActionParamClasses(LSFConfirmActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return inferParamClasses(body.getPropertyExpression(), null).filter(params);
    }

    public static Inferred inferActionParamClasses(LSFAsyncUpdateActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return inferParamClasses(body.getPropertyExpression(), null).filter(params);
    }

    public static Inferred inferActionParamClasses(LSFSeekObjectActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return inferParamClasses(body.getPropertyExpression(), body.getObjectID().getObjectUsage().resolveClass()).filter(params);
    }

    public static Inferred inferActionParamClasses(LSFEmailActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        List<Inferred> list = new ArrayList<Inferred>();

        List<LSFFormActionObjectUsage> objectMap = new ArrayList<LSFFormActionObjectUsage>();
        List<LSFFormEmailToObjects> objects = body.getFormEmailToObjectsList();
        for (LSFFormEmailToObjects object : objects) {
            LSFFormActionObjectList actionObjList = object.getEmailActionFormObjects().getFormActionObjectList();
            if (actionObjList != null)
                for (LSFFormActionObjectUsage ou : actionObjList.getFormActionObjectUsageList())
                    list.add(inferParamClasses(ou.getPropertyExpression(), ou.getObjectUsage().resolveClass()).filter(params));
        }

        for (LSFPropertyExpression pe : body.getPropertyExpressionList())
            list.add(inferParamClasses(pe, null).filter(params));

        return Inferred.orClasses(list);
    }

    public static Inferred inferActionParamClasses(LSFFileActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return inferParamClasses(body.getPropertyExpression(), null).filter(params);
    }

    public static Inferred inferActionParamClasses(LSFEvalActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return inferParamClasses(body.getPropertyExpression(), null).filter(params);
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFNavigatorStatement navigatorStatement, int flags) {
        return LSFIcons.NAVIGATOR_ELEMENT;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFOverrideStatement overrideStatement, int flags) {
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
        return LSFIcons.DESIGN;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFWindowStatement windowStatement, int flags) {
        return LSFIcons.WINDOW;
    }
}
