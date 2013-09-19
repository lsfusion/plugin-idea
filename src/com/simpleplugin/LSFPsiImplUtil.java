package com.simpleplugin;

import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.util.Processor;
import com.simpleplugin.classes.*;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.context.PropExpression;
import com.simpleplugin.psi.context.UnfriendlyPE;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.declarations.LSFExprParamDeclaration;
import com.simpleplugin.psi.declarations.LSFPropDeclaration;
import com.simpleplugin.psi.references.LSFAbstractParamReference;
import com.simpleplugin.psi.references.impl.LSFAbstractParamReferenceImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class LSFPsiImplUtil {

    public static List<PsiElement> getContextModifier(@NotNull LSFPropertyStatement sourceStatement) {
        LSFClassParamDeclareList decl = sourceStatement.getPropertyDeclaration().getClassParamDeclareList();
        if(decl!=null)
            return Collections.<PsiElement>singletonList(decl);
        LSFPropertyExpression expression = sourceStatement.getPropertyExpression();
        if(expression!=null)
            return Collections.<PsiElement>singletonList(expression);

        return new ArrayList<PsiElement>();        
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFOverrideStatement sourceStatement) {
        LSFClassParamDeclareList decl = sourceStatement.getMappedPropertyClassParamDeclare().getClassParamDeclareList();
        return Collections.<PsiElement>singletonList(decl);
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFActionPropertyDefinition sourceStatement) {
        LSFClassParamDeclareList decl = sourceStatement.getClassParamDeclareList();
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
        LSFClassParamDeclareList decl = sourceStatement.getMappedPropertyClassParamDeclare().getClassParamDeclareList();
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
        LSFClassParamDeclareList decl = sourceStatement.getMappedPropertyClassParamDeclare().getClassParamDeclareList();
        return Collections.<PsiElement>singletonList(decl);
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFAspectStatement sourceStatement) {
        LSFMappedPropertyClassParamDeclare decl = sourceStatement.getMappedPropertyClassParamDeclare();
        if(decl==null)
            return new ArrayList<PsiElement>();
        return Collections.<PsiElement>singletonList(decl.getClassParamDeclareList());
    }

    public static List<PsiElement> getContextModifier(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        List<PsiElement> result = new ArrayList<PsiElement>();
        for(LSFNonEmptyPropertyExpressionList exprList : sourceStatement.getNonEmptyPropertyExpressionListList())
            result.addAll(exprList.getPropertyExpressionList());
        LSFGroupPropertyBy by = sourceStatement.getGroupPropertyBy();
        if(by != null)
            result.add(by);
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

    // CLASSES
    
    @Nullable
    private static LSFClassSet or(@Nullable LSFClassSet class1,@Nullable LSFClassSet class2) {
        if(class1==null || class2==null)
            return null;
        
        if(class1 instanceof DataClass) {
            if(class2 instanceof DataClass)
                return ((DataClass) class1).or((DataClass) class2);
            return null;
        }
        if(class2 instanceof DataClass)
            return null;
        
        return ((CustomClassSet)class1).or((CustomClassSet)class2);
    }

    public static boolean containsAll(@NotNull List<LSFClassSet> who, @NotNull List<LSFClassSet> what, boolean falseImplicitClass) {
        for(int i=0,size=who.size();i<size;i++) {
            LSFClassSet whoClass = who.get(i);
            LSFClassSet whatClass = what.get(i);
            if(whoClass==null && whatClass==null)
                continue;
            
            if(whoClass!=null && whatClass!=null && !LSFPsiImplUtil.containsAll(whoClass, whatClass))
                return false;
            
            if(whatClass!=null)
                continue;

            if(falseImplicitClass)
                return false;
        }
        return true;
    }

    public static boolean haveCommonChilds(@NotNull List<LSFClassSet> classes1, @NotNull List<LSFClassSet> classes2) {
        for(int i=0,size=classes1.size();i<size;i++) {
            LSFClassSet class1 = classes1.get(i);
            LSFClassSet class2 = classes2.get(i);
            if(class1!=null && class2!=null && !LSFPsiImplUtil.haveCommonChilds(class1, class2)) // потом переделать haveCommonChilds
                return false;
        }
        return true;
    }
    
    public static boolean containsAll(@NotNull LSFClassSet who, @NotNull LSFClassSet what) {
        if(who instanceof DataClass) {
            if(what instanceof DataClass)
                return ((DataClass)who).or((DataClass) what) != null;
            return false;
        }
        if(what instanceof DataClass)
            return false;
        
        return ((CustomClassSet)who).containsAll((CustomClassSet)what);         
    }

    public static boolean haveCommonChilds(@NotNull LSFClassSet who, @NotNull LSFClassSet what) {
        if(who instanceof DataClass) {
            if(what instanceof DataClass)
                return ((DataClass)who).or((DataClass) what) != null;
            return false;
        }
        if(what instanceof DataClass)
            return false;

        return ((CustomClassSet)who).haveCommonChilds((CustomClassSet)what) != null;
    }

    public static boolean allClassesDeclared(List<LSFClassSet> classes) {
        for(LSFClassSet classSet : classes)
            if(classSet==null)
                return false;
        return true;        
    }
    
    private static LSFClassSet resolve(LSFBuiltInClassName builtIn) {
        String name = builtIn.getText();
        if(name.equals("DOUBLE"))
            return new DoubleClass();
        if(name.equals("INTEGER"))
            return new IntegerClass();
        if(name.equals("LONG"))
            return new LongClass();
        if(name.equals("YEAR"))
            return new YearClass();
        
        if (name.startsWith("STRING[")) {
            name = name.substring("STRING[".length(), name.length() - 1);
            return new StringClass(true, false, new ExtInt(Integer.parseInt(name)));
        } else if (name.startsWith("ISTRING[")) {
            name = name.substring("ISTRING[".length(), name.length() - 1);
            return new StringClass(true, true, new ExtInt(Integer.parseInt(name)));
        } else if (name.startsWith("VARSTRING[")) {
            name = name.substring("VARSTRING[".length(), name.length() - 1);
            return new StringClass(false, false,  new ExtInt(Integer.parseInt(name)));
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
        if(decl == null)
            return null;
        return new CustomClassSet(decl);                 
    }
    @Nullable
    public static LSFClassSet resolveClass(LSFClassName className) {
        LSFBuiltInClassName builtInClassName = className.getBuiltInClassName();
        if(builtInClassName!=null)
            return resolve(builtInClassName);
        return resolveClass(className.getCustomClassUsage());
    }
    public static List<LSFClassSet> resolveClass(LSFClassNameList classNameList) {
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();
                
        LSFNonEmptyClassNameList ne = classNameList.getNonEmptyClassNameList();
        if(ne == null)
            return result;
        for(LSFClassName className : ne.getClassNameList())
            result.add(resolveClass(className));
        return result;
    }
    @Nullable
    public static LSFClassSet resolveClass(@NotNull LSFClassParamDeclare sourceStatement) {
        LSFClassName className = sourceStatement.getClassName();
        if(className == null)
            return null;
        return resolveClass(className);
    }
    public static List<LSFClassSet> resolveClass(LSFClassParamDeclareList classNameList) {
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();

        LSFNonEmptyClassParamDeclareList ne = classNameList.getNonEmptyClassParamDeclareList();
        if(ne == null)
            return result;
        for(LSFClassParamDeclare classParamDeclare : ne.getClassParamDeclareList())
            result.add(resolveClass(classParamDeclare));
        return result;
    }
    @Nullable
    public static LSFClassSet resolveClass(@NotNull LSFForAddObjClause sourceStatement) {
        return resolveClass(sourceStatement.getCustomClassUsage());
    }
    
    // PROPERTYEXPRESSION.RESOLVEVALUECLASS

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFPropertyExpression sourceStatement) {
        return sourceStatement.getIfPE().resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFIfPE sourceStatement) {
        return sourceStatement.getOrPEList().get(0).resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFOrPE sourceStatement) {
        List<LSFXorPE> list = sourceStatement.getXorPEList();
        if(list.size() > 1)
            return DataClass.BOOLEAN;
        return list.get(0).resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFXorPE sourceStatement) {
        List<LSFAndPE> list = sourceStatement.getAndPEList();
        if(list.size() > 1)
            return DataClass.BOOLEAN;
        return list.get(0).resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFAndPE sourceStatement) {
        List<LSFNotPE> list = sourceStatement.getNotPEList();
        if(list.size() > 1)
            return DataClass.BOOLEAN;
        return list.get(0).resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFNotPE sourceStatement) {
        LSFEqualityPE eqPE = sourceStatement.getEqualityPE();
        if(eqPE!=null)
            return eqPE.resolveValueClass();
        return DataClass.BOOLEAN;
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFEqualityPE sourceStatement) {
        List<LSFRelationalPE> list = sourceStatement.getRelationalPEList();
        if(list.size() == 2)
            return DataClass.BOOLEAN;
        return list.get(0).resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFRelationalPE sourceStatement) {
        List<LSFAdditiveORPE> list = sourceStatement.getAdditiveORPEList();
        if(list.size()==2 || sourceStatement.getTypePropertyDefinition()!=null)
            return DataClass.BOOLEAN;
        return list.get(0).resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFAdditiveORPE sourceStatement) {
        return sourceStatement.getAdditivePEList().get(0).resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFAdditivePE sourceStatement) {
        return sourceStatement.getMultiplicativePEList().get(0).resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFMultiplicativePE sourceStatement) {
        return sourceStatement.getUnaryMinusPEList().get(0).resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFUnaryMinusPE sourceStatement) {
        LSFUnaryMinusPE unaryMinusPE = sourceStatement.getUnaryMinusPE();
        if(unaryMinusPE != null)
            return unaryMinusPE.resolveValueClass();
        
        return sourceStatement.getPostfixUnaryPE().resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFPostfixUnaryPE sourceStatement) {
        LSFUintLiteral uintLiteral = sourceStatement.getUintLiteral();
        if(uintLiteral!=null)
            return null;
        
        return sourceStatement.getSimplePE().resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFSimplePE sourceStatement) {
        LSFPropertyExpression pe = sourceStatement.getPropertyExpression();
        if(pe!=null)
            return pe.resolveValueClass();

        return sourceStatement.getExpressionPrimitive().resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFExpressionPrimitive sourceStatement) {
        LSFExprParameterUsage ep = sourceStatement.getExprParameterUsage();
        if(ep!=null)
            return ep.resolveValueClass();

        return sourceStatement.getExpressionFriendlyPD().resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFExprParameterUsage sourceStatement) {
        LSFExprParameterNameUsage nameParam = sourceStatement.getExprParameterNameUsage();
        if(nameParam != null)
            return nameParam.resolveClass();
        return null;
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFExpressionFriendlyPD sourceStatement) {
        return ((PropExpression) sourceStatement.getChildren()[0]).resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFJoinPropertyDefinition sourceStatement) {
        LSFPropertyObject propertyObject = sourceStatement.getPropertyObject();
        LSFPropertyUsage usage = propertyObject.getPropertyUsage();
        if(usage!=null) {
            LSFPropDeclaration decl = usage.resolveDecl();
            if(decl!=null)
                return decl.resolveValueClass();
            return null;
        }

        LSFPropertyExprObject exprObject = propertyObject.getPropertyExprObject();
        LSFPropertyExpression pe = exprObject.getPropertyExpression();
        if(pe!=null)
            return pe.resolveValueClass();
        return exprObject.getExpressionUnfriendlyPD().resolveValueClass();
    }

    @Nullable
    private static LSFClassSet resolveValueClass(List<LSFPropertyExpression> list) {
        if(list.size()==0)
            return null;
        
        LSFClassSet result = null;
        for(int i=0;i<list.size();i++) {
            LSFClassSet valueClass = list.get(i).resolveValueClass();
            if(i==0)
                result = valueClass;
            else
                result = or(result, valueClass);
        }
        return result;
    }

    private static LSFClassSet resolveValueClass(LSFNonEmptyPropertyExpressionList list) {
        return resolveValueClass(list.getPropertyExpressionList());
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFMultiPropertyDefinition sourceStatement) {
        return resolveValueClass(sourceStatement.getNonEmptyPropertyExpressionList());        
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFOverridePropertyDefinition sourceStatement) {
        return resolveValueClass(sourceStatement.getNonEmptyPropertyExpressionList());
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFIfElsePropertyDefinition sourceStatement) {
        List<LSFPropertyExpression> list = sourceStatement.getPropertyExpressionList();
        return resolveValueClass(list.subList(1, list.size()));
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFMaxPropertyDefinition sourceStatement) {
        return resolveValueClass(sourceStatement.getNonEmptyPropertyExpressionList());
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFCasePropertyDefinition sourceStatement) {
        List<LSFPropertyExpression> list = new ArrayList<LSFPropertyExpression>();
        for(LSFCaseBranchBody caseBranch : sourceStatement.getCaseBranchBodyList())
            list.add(caseBranch.getPropertyExpressionList().get(1));
        LSFPropertyExpression elseExpr = sourceStatement.getPropertyExpression();
        if(elseExpr!=null)
            list.add(elseExpr);
        return resolveValueClass(list);
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFPartitionPropertyDefinition sourceStatement) {
        return sourceStatement.getPropertyExpression().resolveValueClass();
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFRecursivePropertyDefinition sourceStatement) {
        return resolveValueClass(sourceStatement.getPropertyExpressionList());
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFStructCreationPropertyDefinition sourceStatement) {
        return DataClass.STRUCT;
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFCastPropertyDefinition sourceStatement) {
        return resolve(sourceStatement.getBuiltInClassName());
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFConcatPropertyDefinition sourceStatement) {
        return resolveValueClass(sourceStatement.getNonEmptyPropertyExpressionList());
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFSessionPropertyDefinition sourceStatement) {
        return DataClass.BOOLEAN;
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFSignaturePropertyDefinition sourceStatement) {
        return DataClass.BOOLEAN;
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFLiteral sourceStatement) {
        if(sourceStatement.getBooleanLiteral() != null)
            return DataClass.BOOLEAN;
        if(sourceStatement.getUlongLiteral() != null)
            return new SimpleDataClass("LONG");
        if(sourceStatement.getUintLiteral() != null)
            return new SimpleDataClass("INTEGER");
        if(sourceStatement.getUdoubleLiteral() != null)
            return new SimpleDataClass("DOUBLE");
        LSFStringLiteral stringLiteral = sourceStatement.getStringLiteral();
        if(stringLiteral != null)
            return new StringClass(false, true, new ExtInt(stringLiteral.getText().length()));
        LSFDateTimeLiteral dateTimeLiteral = sourceStatement.getDateTimeLiteral();
        if(dateTimeLiteral != null)
            return new SimpleDataClass("DATETIME");
        LSFDateLiteral dateLiteral = sourceStatement.getDateLiteral();
        if(dateLiteral != null)
            return new SimpleDataClass("DATE");
        LSFTimeLiteral timeLiteral = sourceStatement.getTimeLiteral();
        if(timeLiteral != null)
            return new SimpleDataClass("TIME");
        LSFColorLiteral colorLiteral = sourceStatement.getColorLiteral();
        if(colorLiteral != null)
            return new SimpleDataClass("COLOR");
        return null;
    }
    
    // UnfriendlyPE.resolveValueClass

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFExpressionUnfriendlyPD sourceStatement) {
        LSFContextIndependentPD contextIndependentPD = sourceStatement.getContextIndependentPD();
        if(contextIndependentPD!=null)
            return ((UnfriendlyPE)contextIndependentPD.getChildren()[0]).resolveValueClass();
        return null;
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFDataPropertyDefinition sourceStatement) {
        return resolveClass(sourceStatement.getClassName());
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFAbstractPropertyDefinition sourceStatement) {
        return resolveClass(sourceStatement.getClassName());
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFAbstractActionPropertyDefinition sourceStatement) {
        return null;
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFGroupPropertyDefinition sourceStatement) {
        return resolveValueClass(sourceStatement.getNonEmptyPropertyExpressionListList().get(0));
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFFormulaPropertyDefinition sourceStatement) {
        LSFBuiltInClassName builtIn = sourceStatement.getBuiltInClassName();
        if(builtIn != null)
            return resolve(builtIn);
        return null;
    }

    @Nullable
    public static LSFClassSet resolveValueClass(@NotNull LSFFilterPropertyDefinition sourceStatement) {
        return DataClass.BOOLEAN;        
    }

    // UnfriendlyPE.resolveValueParamClasses
    
    @Nullable
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFExpressionUnfriendlyPD sourceStatement) {
        LSFContextIndependentPD contextIndependentPD = sourceStatement.getContextIndependentPD();
        if(contextIndependentPD!=null)
            return ((UnfriendlyPE)contextIndependentPD.getChildren()[0]).resolveValueParamClasses();
        return null; // потом с action'ами надо дореализовать
    }

    @Nullable
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFDataPropertyDefinition sourceStatement) {
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
        if(groupBy == null)
            return new ArrayList<LSFClassSet>();

        return resolveParamClasses(groupBy.getNonEmptyPropertyExpressionList());
    }

    @Nullable
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFFormulaPropertyDefinition sourceStatement) {
        String text = sourceStatement.getStringLiteral().getText();
        int i=0;
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();
        while(text.contains("$"+(i+1))) {
            i++;
            result.add(null);
        }
        return result;
    }

    @Nullable
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFFilterPropertyDefinition sourceStatement) {
        return null;
    }

    // LSFPropertyExpression.resolveValueParamClasses

    @Nullable
    public static List<LSFClassSet> resolveValueParamClasses(@NotNull LSFPropertyExpression sourceStatement) {

        final List<LSFClassSet> result = new ArrayList<LSFClassSet>();
        LSFAbstractParamReferenceImpl.processModifyContextParams(sourceStatement, Integer.MAX_VALUE, new HashSet<String>(), new Processor<LSFExprParamDeclaration>() {
            public boolean process(LSFExprParamDeclaration decl) {
                result.add(decl.resolveClass());
                return true;
            }
        });
        return result;
    }

    // PROPERTYUSAGECONTEXT.RESOLVEPARAMCLASSES
    
    public static List<LSFClassSet> resolveParamDeclClasses(List<? extends LSFExprParamDeclaration> decls) {
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();
        for(LSFExprParamDeclaration decl : decls)
            result.add(decl.resolveClass());
        return result;
    }

    public static List<LSFClassSet> resolveParamRefClasses(List<? extends LSFAbstractParamReference> refs) {
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();
        for(LSFAbstractParamReference ref : refs)
            result.add(ref.resolveClass());
        return result;
    }

    public static List<LSFClassSet> resolveParamExprClasses(List<? extends PropExpression> exprs) {
        List<LSFClassSet> result = new ArrayList<LSFClassSet>();
        for(PropExpression expr : exprs)
            result.add(expr.resolveValueClass());
        return result;
    }

    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFClassParamDeclareList sourceStatement) {
        LSFNonEmptyClassParamDeclareList ne = sourceStatement.getNonEmptyClassParamDeclareList();
        if(ne != null) {
            List<LSFClassSet> result = new ArrayList<LSFClassSet>();
            for(LSFClassParamDeclare decl : ne.getClassParamDeclareList())
                result.add(decl.resolveClass());
            return result;
        }
            
        return new ArrayList<LSFClassSet>();
    }

    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFObjectUsageList sourceStatement) {
        LSFNonEmptyObjectUsageList ne = sourceStatement.getNonEmptyObjectUsageList();
        if(ne != null)
            return resolveParamRefClasses(ne.getObjectUsageList());
        
        return new ArrayList<LSFClassSet>();
    }

    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFNonEmptyPropertyExpressionList sourceStatement) {
        return resolveParamExprClasses(sourceStatement.getPropertyExpressionList());
    }

    public static List<LSFClassSet> resolveParamClasses(@NotNull LSFPropertyExpressionList sourceStatement) {
        LSFNonEmptyPropertyExpressionList nonEmpty = sourceStatement.getNonEmptyPropertyExpressionList();
        if(nonEmpty != null)
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
        if(by != null)
            return resolveParamClasses(by.getNonEmptyPropertyExpressionList());
        return new ArrayList<LSFClassSet>();
    }

    
    private static List<LSFFormObjectDeclaration> getObjectDecls(LSFFormCommonGroupObject commonGroup) {
        LSFFormSingleGroupObjectDeclaration singleGroup = commonGroup.getFormSingleGroupObjectDeclaration();
        if(singleGroup != null)
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

    @Nullable
      public static Icon getIcon(@NotNull LSFNavigatorStatement navigatorStatement, int flags) {
        return AllIcons.ObjectBrowser.FlattenPackages;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFOverrideStatement overrideStatement, int flags) {
        return AllIcons.General.OverridingMethod;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFConstraintStatement constraintStatement, int flags) {
        return AllIcons.Ide.Warning_notifications;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFFollowsStatement followsStatement, int flags) {
        return AllIcons.Duplicates.SendToTheRight;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFWriteWhenStatement writeWhenStatement, int flags) {
        return AllIcons.Nodes.PropertyWrite;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFEventStatement eventStatement, int flags) {
        return AllIcons.Actions.Execute;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFShowDepStatement showDepStatement, int flags) {
        return AllIcons.Nodes.DataView;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFGlobalEventStatement globalEventStatement, int flags) {
        return AllIcons.Actions.Execute;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFAspectStatement aspectStatement, int flags) {
        return AllIcons.Actions.Execute;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFLoggableStatement loggableStatement, int flags) {
        return AllIcons.FileTypes.Archive;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFIndexStatement indexStatement, int flags) {
        return AllIcons.Graph.PrintPreview;
    }

    @Nullable
    public static Icon getIcon(@NotNull LSFDesignStatement designStatement, int flags) {
        return AllIcons.Actions.Edit;
    }
}
