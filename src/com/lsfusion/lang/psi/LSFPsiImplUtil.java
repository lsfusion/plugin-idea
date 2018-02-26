package com.lsfusion.lang.psi;

import com.intellij.lang.ASTFactory;
import com.intellij.lang.ASTNode;
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
import com.lsfusion.lang.psi.impl.LSFPropertyExpressionListImpl;
import com.lsfusion.lang.psi.impl.LSFSeekObjectActionPropertyDefinitionBodyImpl;
import com.lsfusion.lang.psi.references.LSFAbstractParamReference;
import com.lsfusion.lang.typeinfer.*;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

import static com.lsfusion.lang.psi.LSFReflectionType.CANONICAL_NAME_VALUE_CLASS;
import static java.util.Collections.singletonList;

@SuppressWarnings("UnusedParameters")
public class LSFPsiImplUtil {

    public static ContextModifier getContextAPModifier(@Nullable LSFPropertyCalcStatement calcStatement, @Nullable LSFActionUnfriendlyPD actionUnfriendlyPD, LSFListActionPropertyDefinitionBody listAction) {
        if(calcStatement != null) {
            LSFPropertyExpression expression = calcStatement.getPropertyExpression();
            if (expression != null)
                return new ExprsContextModifier(expression);
        }

        return ContextModifier.EMPTY;
    }
    
    public static ContextInferrer getContextAPInferrer(@Nullable LSFPropertyCalcStatement calcStatement, @Nullable LSFActionUnfriendlyPD actionStatement, LSFListActionPropertyDefinitionBody body) {
        if(calcStatement != null) {
            LSFPropertyExpression expression = calcStatement.getPropertyExpression();
            if (expression != null)
                return new ExprsContextInferrer(expression);
        } else {
            if(body != null)
                return new ActionInferrer(body);
        }

        return ContextInferrer.EMPTY;
    }

    public static ContextModifier getContextModifier(@NotNull LSFPropertyStatement sourceStatement) {
        LSFPropertyDeclParams decl = sourceStatement.getPropertyDeclaration().getPropertyDeclParams();
        if (decl != null)
            return new ExplicitContextModifier(decl.getClassParamDeclareList());

        return getContextAPModifier(sourceStatement.getPropertyCalcStatement(), sourceStatement.getActionUnfriendlyPD(), sourceStatement.getListActionPropertyDefinitionBody());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFPropertyStatement sourceStatement) {
        return getContextAPInferrer(sourceStatement.getPropertyCalcStatement(), sourceStatement.getActionUnfriendlyPD(), sourceStatement.getListActionPropertyDefinitionBody());
    }

    public static ContextModifier getContextModifier(@NotNull LSFOverrideStatement sourceStatement) {
        return new ExplicitContextModifier(sourceStatement.getMappedPropertyClassParamDeclare());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFOverrideStatement sourceStatement) {

        List<LSFPropertyExpression> peList = sourceStatement.getPropertyExpressionList();
        ContextInferrer result = peList.isEmpty() ? ContextInferrer.EMPTY : new ExprsContextInferrer(peList);

        LSFListActionPropertyDefinitionBody body = sourceStatement.getListActionPropertyDefinitionBody();
        if (body != null) {
            ActionExprInferrer actionInfer = new ActionExprInferrer(body);

            result = peList.isEmpty() ? actionInfer : new OverrideContextInferrer(actionInfer, result);
        }
        return result;
    }

    public static ContextModifier getContextModifier(@NotNull LSFConstraintStatement sourceStatement) {
        LSFPropertyExpression decl = sourceStatement.getPropertyExpressionList().get(0);
        if (decl == null)
            return ContextModifier.EMPTY;
        return new ExprsContextModifier(decl);
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFConstraintStatement sourceStatement) {
        LSFPropertyExpression decl = sourceStatement.getPropertyExpressionList().get(0);
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
        List<LSFPropertyExpression> result = new ArrayList<>();
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
        return getContextAPModifier(sourceStatement.getPropertyCalcStatement(), null, null); // sourceStatement.getActionUnfriendlyPD(), sourceStatement.getListActionPropertyDefinitionBody());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFPropertyExprObject sourceStatement) {
        return getContextAPInferrer(sourceStatement.getPropertyCalcStatement(), null, null); //sourceStatement.getActionUnfriendlyPD(), sourceStatement.getListActionPropertyDefinitionBody());
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

    public static ContextModifier getContextModifier(@NotNull LSFWhileActionPropertyDefinitionBody sourceStatement) {
        return new ExprsContextModifier(sourceStatement.getPropertyExpression());
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFWhileActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getInputContextModifier(@NotNull LSFDialogActionPropertyDefinitionBody sourceStatement) {
        ContextModifier result = null;

        LSFFormActionObjectList formList = sourceStatement.getFormActionObjectList();
        if(formList != null) {
            for(LSFFormActionObjectUsage object : formList.getFormActionObjectUsageList()) {
                if(object.getObjectInputProps() != null) {
                    ContextModifier inputModifier = new InputContextModifier(object);
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

    public static ContextInferrer getInputContextInferrer(@NotNull LSFDialogActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getInputContextModifier(@NotNull LSFInputActionPropertyDefinitionBody sourceStatement) {
        return new InputContextModifier(sourceStatement.getParamDeclare());
    }

    public static ContextModifier getContextModifier(@NotNull LSFDoMainBody sourceStatement) {
        ExtendInputParamContext context = PsiTreeUtil.getParentOfType(sourceStatement, ExtendInputParamContext.class);
        return context == null ? null : context.getInputContextModifier();
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFDoMainBody sourceStatement) {
        ExtendInputParamContext context = PsiTreeUtil.getParentOfType(sourceStatement, ExtendInputParamContext.class);
        return context == null ? null : context.getInputContextInferrer();
    }

    public static ContextInferrer getInputContextInferrer(@NotNull LSFInputActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getInputContextModifier(@NotNull LSFConfirmActionPropertyDefinitionBody sourceStatement) {
        return new InputContextModifier(sourceStatement.getParamDeclare());
    }

    public static ContextInferrer getInputContextInferrer(@NotNull LSFConfirmActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getContextModifier(@NotNull LSFAssignActionPropertyDefinitionBody sourceStatement) {
        LSFMappedPropertyExprParam mappedPropertyExprParam = sourceStatement.getMappedPropertyExprParam();
        if (mappedPropertyExprParam != null) {
            LSFExprParameterUsageList exprParameterUsageList = mappedPropertyExprParam.getExprParameterUsageList();
            if (exprParameterUsageList != null) {
                return new ExprParamUsageContextModifier(exprParameterUsageList.getExprParameterUsageList());
            }
        }
        return new ExprParamUsageContextModifier(Collections.<LSFExprParameterUsage>emptyList());
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
        LSFPropertyExpression where = sourceStatement.getPropertyExpression();
        if(where != null)
            result.add(where);
        return result;
    }

    public static ContextModifier getContextModifier(@NotNull LSFExportDataActionPropertyDefinitionBody sourceStatement) {
        return new ExprsContextModifier(getContextExprs(sourceStatement));
    }
    
    public static ContextInferrer getContextInferrer(@NotNull LSFExportDataActionPropertyDefinitionBody sourceStatement) {
        return new ExprsContextInferrer(getContextExprs(sourceStatement));
    }

    public static ContextModifier getContextModifier(@NotNull LSFChangeClassActionPropertyDefinitionBody sourceStatement) {
        return getContextModifier(sourceStatement.getExprParameterUsage());
    }

    private static ContextModifier getContextModifier(LSFExprParameterUsage exprParameterUsage) {
        return new ExprParamUsageContextModifier(exprParameterUsage == null ? Collections.<LSFExprParameterUsage>emptyList() : singletonList(exprParameterUsage));
    }

    public static ContextInferrer getContextInferrer(@NotNull LSFChangeClassActionPropertyDefinitionBody sourceStatement) {
        return new ActionExprInferrer(sourceStatement);
    }

    public static ContextModifier getContextModifier(@NotNull LSFDeleteActionPropertyDefinitionBody sourceStatement) {
        return getContextModifier(sourceStatement.getExprParameterUsage());
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
        } else if (name.equals("TEXT")) {
            return new StringClass(false, false, false, ExtInt.UNLIMITED);
        } else if (name.equals("RICHTEXT")) {
            return new StringClass(false, false, true, ExtInt.UNLIMITED);
        }

        switch (name) {
            case "WORDFILE":
                return WordClass.instance;
            case "IMAGEFILE":
                return ImageClass.instance;
            case "PDFFILE":
                return PDFClass.instance;
            case "CUSTOMFILE":
                return DynamicFormatFileClass.instance;
            case "EXCELFILE":
                return ExcelClass.instance;
            case "WORDLINK":
                return WordLinkClass.instance;
            case "IMAGELINK":
                return ImageLinkClass.instance;
            case "PDFLINK":
                return PDFLinkClass.instance;
            case "CUSTOMLINK":
                return DynamicFormatLinkClass.instance;
            case "EXCELLINK":
                return ExcelLinkClass.instance;
            case "BOOLEAN":
                return LogicalClass.instance;
            case "DATE":
                return DateClass.instance;
            case "TIME":
                return TimeClass.instance;
            case "DATETIME":
                return DateTimeClass.instance;
            case "COLOR":
                return ColorClass.instance;
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
            LSFGlobalPropDeclaration statement = PsiTreeUtil.getParentOfType(sourceStatement, LSFGlobalPropDeclaration.class);
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
    public static LSFClassSet resolveClass(@NotNull LSFInputActionPropertyDefinitionBody sourceStatement) {
        LSFBuiltInClassName builtInClassName = sourceStatement.getBuiltInClassName();
        if(builtInClassName != null)
            return resolve(builtInClassName);

        List<LSFPropertyExpression> list = sourceStatement.getPropertyExpressionList();
        if(!list.isEmpty()) {
            LSFPropertyExpression pe = list.get(0);
            if (pe != null)
                return LSFExClassSet.fromEx(pe.resolveValueClass(false));
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

        LSFExClassSet result = resolveInferredValueClass(list.get(0), inferred);

        LSFTypePropertyDefinition typeDef = sourceStatement.getTypePropertyDefinition();
        if (typeDef != null) {
            if (typeDef.getTypeIs().getText().equals("IS"))
                return LSFExClassSet.logical;
            else {
                LSFExClassSet resolveClass = LSFExClassSet.toEx(resolveClass(typeDef.getClassName()));
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
                else if (hasString) {
                    ex = new LSFExClassSet(new StringClass(false, false, new ExtInt(1)));
                }
            }
            fixedClasses.add(ex);
        }
        return orClasses(fixedClasses, true);
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
                return LSFExClassSet.checkNull(((ConcatenateClassSet) classSet).getSet(Integer.valueOf(uintLiteral.getText()) - 1), valueClass.orAny);
            return null;
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
        LSFPropertyObject propertyObject = sourceStatement.getPropertyObject();
        LSFPropertyUsage usage = propertyObject.getPropertyUsage();
        if (usage != null) {
            LSFExClassSet valueClass = resolveValueClass(usage, inferred != null);
            if (valueClass != null)
                return valueClass;
            return null;
        }

        LSFPropertyExprObject exprObject = propertyObject.getPropertyExprObject();

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
        return resolveInferredValueClass(sourceStatement.getNonEmptyPropertyExpressionList(), inferred, true);
    }

    @Nullable
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFSessionPropertyDefinition sourceStatement, @Nullable InferExResult inferred) {
        LSFSessionPropertyType type = sourceStatement.getSessionPropertyType();
        if (type.getText().equals("PREV"))
            return resolveInferredValueClass(sourceStatement.getPropertyExpression(), inferred);
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
    public static LSFExClassSet resolveInferredValueClass(@NotNull LSFLiteral sourceStatement, @Nullable InferExResult inferred) {
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
    public static DataClass resolveBuiltInValueClass(@NotNull LSFLiteral sourceStatement) {
        if (sourceStatement.getBooleanLiteral() != null)
            return LogicalClass.instance;
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
        LSFLocalizedStringValueLiteral stringLiteral = sourceStatement.getLocalizedStringLiteral();
        if (stringLiteral != null) {
            if (stringLiteral.needToBeLocalized()) {
                return new StringClass(false, false, false, ExtInt.UNLIMITED);
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
        LSFExClassSet lsfExClassSet = resolveInferredValueClass(sourceStatement.getNonEmptyPropertyExpressionList(), infer ? inferGroupParamClasses(sourceStatement) : null);
        LSFGroupingType groupingType = sourceStatement.getGroupingType();
        if(groupingType.getText().equals("CONCAT"))
            lsfExClassSet = lsfExClassSet.extend(10);
        return lsfExClassSet;
    }

    @Nullable
    public static LSFExClassSet resolveUnfriendValueClass(@NotNull LSFFormulaPropertyDefinition sourceStatement, boolean infer) {
        LSFBuiltInClassName builtIn = sourceStatement.getBuiltInClassName();
        if (builtIn != null)
            return LSFExClassSet.toEx(resolve(builtIn));
        return null;
    }

    @Nullable
    public static LSFExClassSet resolveUnfriendValueClass(@NotNull LSFFilterPropertyDefinition sourceStatement, boolean infer) {
        return LSFExClassSet.logical;
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

        List<String> result = getValueClassNames(likePEs.get(0));

        LSFTypePropertyDefinition typePD = sourceStatement.getTypePropertyDefinition();
        if (typePD != null) {
            if (typePD.getTypeIs().getText().equals("IS")) {
                return singletonList(LogicalClass.instance.getName());
            } else {
                String typeDefClass = getClassName(typePD.getClassName());
                if (typeDefClass != null) {
                    result = singletonList(typeDefClass);
                }
            }
        }
        return result;
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
        LSFBuiltInClassName builtIn = sourceStatement.getBuiltInClassName();
        if (builtIn != null) {
            return singletonList(builtIn.getName());
        }
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValueClassNames(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        return getValueClassNames(sourceStatement.getNonEmptyPropertyExpressionList());
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
        LSFPropertyObject propertyObject = sourceStatement.getPropertyObject();
        LSFPropertyUsage usage = propertyObject.getPropertyUsage();
        if (usage != null) {
            return Collections.EMPTY_LIST;
        }

        LSFPropertyExprObject exprObject = propertyObject.getPropertyExprObject();

        assert exprObject != null;

        LSFPropertyCalcStatement pCalcStatement = exprObject.getPropertyCalcStatement();
        return getValueAPClassNames(pCalcStatement, null, null); //exprObject.getActionUnfriendlyPD(), exprObject.getListActionPropertyDefinitionBody());
    }

    public static List<String> getValueAPClassNames(LSFPropertyCalcStatement pCalcStatement, LSFActionUnfriendlyPD unfriendlyPD, LSFListActionPropertyDefinitionBody listPD) {
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

    public static List<String> getValueClassNames(@NotNull LSFLiteral sourceStatement) {
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

        LSFTypePropertyDefinition typePD = sourceStatement.getTypePropertyDefinition();
        if (typePD != null) {
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

    public static List<String> getValuePropertyNames(@NotNull LSFFilterPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFReflectionPropertyDefinition sourceStatement) {
        return Collections.EMPTY_LIST;
    }

    public static List<String> getValuePropertyNames(@NotNull LSFExpressionFriendlyPD sourceStatement) {
        return ((LSFExpression) sourceStatement.getFirstChild()).getValuePropertyNames();
    }

    public static List<String> getValuePropertyNames(@NotNull LSFJoinPropertyDefinition sourceStatement) {
        LSFPropertyObject propertyObject = sourceStatement.getPropertyObject();
        LSFPropertyUsage usage = propertyObject.getPropertyUsage();
        if (usage != null) {
            return singletonList(usage.getName());
        }

        LSFPropertyExprObject exprObject = propertyObject.getPropertyExprObject();

        assert exprObject != null;

        return getValueAPPropertyNames(exprObject.getPropertyCalcStatement(), null, null); //exprObject.getActionUnfriendlyPD(), exprObject.getListActionPropertyDefinitionBody());
    }

    public static List<String> getValueAPPropertyNames(LSFPropertyCalcStatement pCalcStatement, LSFActionUnfriendlyPD unfriendlyPD, LSFListActionPropertyDefinitionBody listActionBody) {
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

    public static List<String> getValuePropertyNames(@NotNull LSFLiteral sourceStatement) {
        return Collections.EMPTY_LIST;
    }


    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFExpressionUnfriendlyPD sourceStatement) {
        return ((UnfriendlyPE) sourceStatement.getChildren()[0]).resolveValueParamClasses();
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(LSFActionUnfriendlyPD unfriendlyPD, LSFListActionPropertyDefinitionBody listBody) {
        if(unfriendlyPD != null)
            return LSFPsiImplUtil.resolveValueParamClasses(unfriendlyPD);
        return null;
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFActionUnfriendlyPD actionDef) {
        return ((UnfriendlyPE) actionDef.getChildren()[0]).resolveValueParamClasses();
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFDataPropertyDefinition sourceStatement) {
        return LSFExClassSet.toEx(resolveClasses(sourceStatement.getClassNameList()));
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFNativePropertyDefinition sourceStatement) {
        return LSFExClassSet.toEx(resolveClasses(sourceStatement.getClassNameList()));
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFAbstractPropertyDefinition sourceStatement) {
        return LSFExClassSet.toEx(resolveClasses(sourceStatement.getClassNameList()));
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFAbstractActionPropertyDefinition sourceStatement) {
        return LSFExClassSet.toEx(resolveClasses(sourceStatement.getClassNameList()));
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFCustomActionPropertyDefinitionBody sourceStatement) {
        LSFClassNameList classNameList = sourceStatement.getClassNameList();
        if (classNameList != null)
            return LSFExClassSet.toEx(resolveClasses(classNameList));
        return null;
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFExternalActionPropertyDefinitionBody sourceStatement) {
        return null;
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        LSFGroupPropertyBy groupBy = sourceStatement.getGroupPropertyBy();
        if (groupBy == null)
            return new ArrayList<>();

        return resolveParamClasses(groupBy.getNonEmptyPropertyExpressionList());
    }

    @NotNull
    public static List<LSFExClassSet> inferGroupValueParamClasses(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        LSFGroupPropertyBy groupBy = sourceStatement.getGroupPropertyBy();
        if (groupBy == null)
            return new ArrayList<>();

        return resolveParamClasses(groupBy.getNonEmptyPropertyExpressionList(), inferGroupParamClasses(sourceStatement));
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFFormulaPropertyDefinition sourceStatement) {
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

        String text = stringLiteral.getValue();
        int i = 0;
        List<LSFExClassSet> result = new ArrayList<>();
        while (text.contains("$" + (i + 1))) {
            i++;
            result.add(null);
        }
        return result;
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFFilterPropertyDefinition sourceStatement) {
        LSFGroupObjectUsage groupObjectUsage = sourceStatement.getGroupObjectID().getGroupObjectUsage();
        if (groupObjectUsage != null) {
            return LSFExClassSet.toEx(groupObjectUsage.resolveClasses());
        }
        return null;
    }

    @Nullable
    public static List<LSFExClassSet> resolveValueParamClasses(@NotNull LSFReflectionPropertyDefinition sourceStatement) {
        return null;
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
        LSFClassNameList classNameList = sourceStatement.getClassNameList();
        if(classNameList != null)
            return getClassNameRefs(classNameList);
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
            for (LSFPropertyExpression pe : groupPropertyBy.getNonEmptyPropertyExpressionList().getPropertyExpressionList()) {
                valueClasses.addAll(getValueClassNames(pe));
            }
            return LSFImplicitExplicitClasses.create(valueClasses);
        }
        return new LSFExplicitSignature(Collections.EMPTY_LIST);
    }

    public static LSFExplicitClasses getValueParamClassNames(@NotNull LSFFilterPropertyDefinition sourceStatement) {
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
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFPropertyExpression sourceStatement) {
        List<LSFClassSet> result = new ArrayList<>();
        for (LSFExprParamDeclaration param : sourceStatement.resolveParams())
            result.add(param.resolveClass());
        return result;
    }

    @NotNull
    public static List<LSFExprParamDeclaration> resolveParams(@NotNull LSFPropertyExpression sourceStatement) {
        return new ExprsContextModifier(sourceStatement).resolveParams(Integer.MAX_VALUE, new HashSet<LSFExprParamDeclaration>());
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
        if (propertyDeclaration.getPropertyDeclParams() != null) {
            LSFNonEmptyClassParamDeclareList paramDeclareList = propertyDeclaration.getPropertyDeclParams().getClassParamDeclareList().getNonEmptyClassParamDeclareList();
            if (paramDeclareList != null) {
                result = new ArrayList<>();
                for (LSFClassParamDeclare paramDeclare : paramDeclareList.getClassParamDeclareList())
                    result.add(paramDeclare.getParamDeclare());
            }
        }
        return result;
    }

    // PROPERTYUSAGECONTEXT.RESOLVEPARAMCLASSES

    @NotNull
    public static List<LSFClassSet> resolveParamDeclClasses(Collection<? extends LSFExprParamDeclaration> decls) {
        List<LSFClassSet> result = new ArrayList<>();
        for (LSFExprParamDeclaration decl : decls) {
            LSFClassSet classSet = decl.resolveClass();
            if (classSet != null) {
                result.add(classSet);
            }
        }
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
    public static List<LSFExClassSet> resolveParamClasses(@NotNull LSFNonEmptyPropertyExpressionList sourceStatement, @Nullable InferExResult inferred) {
        return resolveExprClasses(sourceStatement.getPropertyExpressionList(), inferred);
    }

    public static List<LSFExClassSet> resolveParamClasses(@NotNull LSFNonEmptyPropertyExpressionList sourceStatement) {
        return resolveExprClasses(sourceStatement.getPropertyExpressionList(), null);
    }

    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFPropertyExpressionList sourceStatement) {
        return finishParamClasses(sourceStatement.getNonEmptyPropertyExpressionList());
    }

    private static List<LSFClassSet> finishParamClasses(LSFNonEmptyPropertyExpressionList nonEmpty) {
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
        LSFNonEmptyPropertyExpressionList nepeList = peList.getNonEmptyPropertyExpressionList();
        if(nepeList == null)
            return new ArrayList<>();
        else
            return nepeList.getPropertyExpressionList();
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
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFNoContextPropertyUsage sourceStatement) {
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
    public static PsiElement getParamList(@NotNull LSFExecActionPropertyDefinitionBody sourceStatement) {
        return sourceStatement.getPropertyExpressionList();
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
            return singletonList(singleGroup.getFormObjectDeclaration());

        LSFFormMultiGroupObjectDeclaration formMultiGroupObjectDeclaration = commonGroup.getFormMultiGroupObjectDeclaration();

        assert formMultiGroupObjectDeclaration != null;

        return formMultiGroupObjectDeclaration.getFormObjectDeclarationList();
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFFormTreeGroupObjectDeclaration sourceStatement) {
        return resolveParamDeclClasses(getObjectDecls(sourceStatement.getFormCommonGroupObject()));
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
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFMappedPropertyClassParamDeclare sourceStatement) {
        return resolveParamClasses(sourceStatement.getClassParamDeclareList());
    }

    @Nullable
    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFMappedPropertyExprParam sourceStatement) {
        LSFExprParameterUsageList exprParameterUsageList = sourceStatement.getExprParameterUsageList();
        return exprParameterUsageList == null ? null : LSFExClassSet.fromEx(resolveParamClasses(exprParameterUsageList));
    }

    public static List<LSFClassSet> resolveParamClasses(LSFImportPropertyUsage sourceStatement) {

        LSFImportActionPropertyDefinitionBody importDB = PsiTreeUtil.getParentOfType(sourceStatement, LSFImportActionPropertyDefinitionBody.class);
        if(importDB != null) {
            LSFImportActionSourceType sourceType = importDB.getImportActionSourceType();
            if(sourceType != null) {
                String importType = sourceType.getText();
                if (importType != null && importType.contains("XML") && importType.contains("LIST"))
                    return Collections.emptyList();
            }
        }
        return Collections.singletonList(IntegerClass.instance);
    }

    @Nullable
    public static PsiElement getParamList(@NotNull LSFImportPropertyUsage sourceStatement) {
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

        LSFTypePropertyDefinition typeDef = sourceStatement.getTypePropertyDefinition();
        if (typeDef != null)
            return inferParamClasses(list.get(0), LSFExClassSet.toEx(resolveClass(typeDef.getClassName())));

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
                valueClass = LSFExClassSet.checkNull(((ConcatenateClassSet) valueClass.classSet).getSet(Integer.valueOf(uintLiteral.getText()) - 1), valueClass.orAny);
            else
                valueClass = null;
        }

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

    public static Inferred inferJoinParamClasses(@NotNull LSFPropertyObject propertyObject, LSFPropertyExpressionList peList, @Nullable LSFExClassSet valueClass) {
        List<LSFExClassSet> joinClasses = inferParamClasses(valueClass, propertyObject);

        List<LSFPropertyExpression> list = getList(peList);

        List<Inferred> classes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
            classes.add(inferParamClasses(list.get(i), joinClasses != null && i < joinClasses.size() ? joinClasses.get(i) : null));
        return Inferred.andClasses(classes);

    }

    @NotNull
    public static Inferred inferParamClasses(@NotNull LSFJoinPropertyDefinition sourceStatement, @Nullable LSFExClassSet valueClass) {
        return inferJoinParamClasses(sourceStatement.getPropertyObject(), sourceStatement.getPropertyExpressionList(), valueClass);
    }

    @Nullable
    private static List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass, LSFPropertyUsage usage) {
        LSFPropDeclaration decl = usage.resolveDecl();
        if (decl != null)
            return decl.inferParamClasses(valueClass);
        return null;
    }

    private static List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass, LSFPropertyObject propertyObject) {
        LSFPropertyUsage usage = propertyObject.getPropertyUsage();
        List<LSFExClassSet> joinClasses;
        if (usage != null) {
            joinClasses = inferParamClasses(valueClass, usage);
        } else {
            LSFPropertyExprObject exprObject = propertyObject.getPropertyExprObject();

            assert exprObject != null;

            LSFPropertyCalcStatement pCalcStatement = exprObject.getPropertyCalcStatement();
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
                    joinClasses = unfriendlyPD.resolveValueParamClasses();
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
    public static Inferred inferParamClasses(@NotNull LSFLiteral sourceStatement, @Nullable LSFExClassSet valueClass) {
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
        LSFMappedPropertyExprParam peTo = body.getMappedPropertyExprParam();

        assert peTo != null; // т.к. !peList.isEmpty()

        Inferred result = inferParamClasses(peTo, peValue.resolveValueClass(true)).filter(params);
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
        LSFPropertyExpression expr = body.getPropertyExpression();
        Inferred result = Inferred.EMPTY;
        if (expr != null)
            result = result.and(inferParamClasses(expr, null).filter(params));
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

    public static Inferred inferActionParamClasses(LSFChangeClassActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а
        return inferExpressionParamClasses(body.getPropertyExpression(), null).filter(params);
    }

    public static Inferred inferActionParamClasses(LSFDeleteActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        // берем условия for, если есть, для остальных из внутреннего action'а
        return inferExpressionParamClasses(body.getPropertyExpression(), null).filter(params);
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
        Inferred inputInferred = new ExprsContextInferrer(body.getPropertyExpressionList()).inferClasses(params);
        return inferDoInputBody(body.getDoInputBody(), inputInferred, params);
    }

    public static Inferred inferActionParamClasses(LSFActiveFormActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFActivateActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return Inferred.EMPTY;
    }

    public static Inferred inferActionParamClasses(LSFExecActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        LSFPropertyObject propertyObject = body.getPropertyObject();
        if (propertyObject == null) {
            return Inferred.EMPTY;
        }
        return inferJoinParamClasses(propertyObject, body.getPropertyExpressionList(), null).filter(params);
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
        return inferExpressionParamClasses(body.getPropertyExpression(), null).filter(params);
    }

    public static Inferred inferActionParamClasses(LSFConfirmActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        Inferred inputInferred = inferExpressionParamClasses(body.getPropertyExpression(), null).filter(params);
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

    public static Inferred inferActionParamClasses(LSFEmailActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        List<Inferred> list = new ArrayList<>();

        List<LSFFormEmailToObjects> objects = body.getFormEmailToObjectsList();
        for (LSFFormEmailToObjects object : objects) {
            LSFFormActionObjectList actionObjList = object.getFormActionObjectList();
            if (actionObjList != null) {
                for (LSFFormActionObjectUsage ou : actionObjList.getFormActionObjectUsageList()) {
                    LSFObjectInProps objectInProps = ou.getObjectInProps();
                    if(objectInProps != null) {
                        list.add(inferExpressionParamClasses(objectInProps.getPropertyExpression(), LSFExClassSet.toEx(ou.getObjectUsage().resolveClass())).filter(params));
                    }
                }
            }
        }

        for (LSFPropertyExpression pe : body.getPropertyExpressionList()) {
            list.add(inferParamClasses(pe, null).filter(params));
        }

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

    public static Inferred inferActionParamClasses(LSFFileActionPropertyDefinitionBody body, @Nullable Set<LSFExprParamDeclaration> params) {
        return inferExpressionParamClasses(body.getPropertyExpressionList().get(0), null).filter(params);
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
        return LSFIcons.Design.DESIGN;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFWindowStatement windowStatement, int flags) {
        return LSFIcons.WINDOW;
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
    public static LSFFormDeclaration resolveFormDecl(@NotNull LSFFormEmailToObjects formEmailToObjects) {
        return resolveFormDecl(formEmailToObjects.getFormUsage());
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

    @NotNull
    public static List<String> getMigrationClassNames(LSFDataPropertyDefinition dataPropertyDefinition) {
        LSFClassNameList classNameList = dataPropertyDefinition.getClassNameList();

        if (classNameList == null) {
            return Collections.emptyList();
        }
        
        LSFNonEmptyClassNameList ne = classNameList.getNonEmptyClassNameList();
        if (ne == null) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();
        for (LSFClassName className : ne.getClassNameList()) {
            result.add(getMigrationClassName(className));
        }
        return result;
    }

    public static String getMigrationClassName(LSFClassName className) {
        LSFBuiltInClassName builtIn = className.getBuiltInClassName();
        if (builtIn != null) {
            return getMigrationClassName(builtIn);
        }
        
        LSFCustomClassUsage customClassUsage = className.getCustomClassUsage();
        
        assert customClassUsage != null;
        
        return getMigrationClassName(customClassUsage);
    }

    private static String getMigrationClassName(LSFCustomClassUsage customClassUsage) {
        String nameRef = customClassUsage.getNameRef();
        
        String namespaceRef = customClassUsage.getFullNameRef();
        if (namespaceRef != null) {
            return namespaceRef + "." + nameRef;
        }
        
        LSFClassDeclaration classDecl = customClassUsage.resolveDecl();
        if (classDecl == null) {
            //can't resolve -> just use the reference text
            return nameRef;
        }
        
        return classDecl.getNamespaceName() + "." + classDecl.getGlobalName();
    }

    public static String getMigrationClassName(LSFBuiltInClassName builtInClassName) {
        String name = builtInClassName.getName();
        if (name == null) {
            //shouldn't happen
            return "?";
        }

        if (name.startsWith("NUMERIC")) {
            return "NUMERIC";
        }

        if (name.startsWith("STRING") ||
            name.startsWith("VARSTRING") ||
            name.startsWith("ISTRING") ||
            name.startsWith("VARISTRING") ||
            name.equals("TEXT") ||
            name.equals("RICHTEXT")) {
            return "STRING";
        }

        return name;
    }

    public static boolean checkOverrideValue(@NotNull LSFOverrideStatement o, Result<LSFClassSet> required, Result<LSFClassSet> found) {
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
    
    public static boolean checkNonRecursiveOverride(@NotNull LSFOverrideStatement override) {
        LSFPropertyUsage leftUsage = override.getMappedPropertyClassParamDeclare().getPropertyUsageWrapper().getPropertyUsage();

        int leftParams = 0;
        LSFClassParamDeclareList classParamDeclareList = override.getMappedPropertyClassParamDeclare().getClassParamDeclareList();
        if(classParamDeclareList != null) {
            LSFNonEmptyClassParamDeclareList nonEmptyClassParamDeclareList = classParamDeclareList.getNonEmptyClassParamDeclareList();
            leftParams = nonEmptyClassParamDeclareList == null ? 0 : nonEmptyClassParamDeclareList.getClassParamDeclareList().size();
        }

        Collection<LSFJoinPropertyDefinition> joinProps = PsiTreeUtil.findChildrenOfType(override, LSFJoinPropertyDefinition.class);
        for (LSFJoinPropertyDefinition joinProp : joinProps) {
            LSFPropertyUsage rightUsage = joinProp.getPropertyObject().getPropertyUsage();
            if (equalReferences(leftUsage, rightUsage)) {
                if (equalParams(leftParams, joinProp.getParamList()))
                    return false;
            }
        }

        Collection<LSFExecActionPropertyDefinitionBody> execActions = PsiTreeUtil.findChildrenOfType(override, LSFExecActionPropertyDefinitionBody.class);
        for (LSFExecActionPropertyDefinitionBody execAction : execActions) {
            LSFPropertyObject propertyObject = execAction.getPropertyObject();
            if (propertyObject != null) {
                LSFPropertyUsage rightUsage = propertyObject.getPropertyUsage();
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
        }
        return true;
    }

    private static boolean equalReferences(@NotNull LSFPropertyUsage left, LSFPropertyUsage right) {
        return right != null && Objects.equals(left.resolve(), right.resolve());
    }

    private static boolean equalParams(int leftParams, PsiElement paramList) {
        if (paramList != null && paramList instanceof LSFPropertyExpressionListImpl) {
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
}
