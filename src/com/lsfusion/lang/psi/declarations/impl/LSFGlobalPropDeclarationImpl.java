package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.CollectionQuery;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.ColumnNamingPolicy;
import com.lsfusion.lang.classes.CustomClassSet;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.cache.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.indexes.TableClassesIndex;
import com.lsfusion.lang.psi.references.LSFActionOrPropReference;
import com.lsfusion.lang.psi.references.LSFPropReference;
import com.lsfusion.lang.psi.stubs.PropStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import com.lsfusion.refactoring.ElementMigration;
import com.lsfusion.refactoring.PropertyMigration;
import com.lsfusion.util.BaseUtils;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public abstract class LSFGlobalPropDeclarationImpl extends LSFActionOrGlobalPropDeclarationImpl<LSFGlobalPropDeclaration, PropStubElement> implements LSFGlobalPropDeclaration {

    public static final String TABLE_BASE_PREFIX = "base";
    public static final String TABLE_BASE0 = TABLE_BASE_PREFIX + 0;

    public LSFGlobalPropDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFGlobalPropDeclarationImpl(@NotNull PropStubElement propStubElement, @NotNull IStubElementType nodeType) {
        super(propStubElement, nodeType);
    }

    public abstract LSFPropertyDeclaration getPropertyDeclaration();

    @Nullable
    public LSFPropertyCalcStatement getPropertyCalcStatement() {
        LSFPropertyStatementBody body = getPropertyStatementBody();
        if(body != null)
            return body.getPropertyCalcStatement();
        return null;
    }

    @Nullable
    public abstract LSFPropertyStatementBody getPropertyStatementBody();

    @Nullable
    public abstract LSFNonEmptyPropertyOptions getNonEmptyPropertyOptions();

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
    public LSFExClassSet resolveExValueClass(boolean infer) {
        return ValueClassCache.getInstance(getProject()).resolveValueClassWithCaching(this, infer);
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
    protected List<LSFExClassSet> resolveValueParamClasses() {
        LSFPropertyCalcStatement pCalcStatement = getPropertyCalcStatement();
        if(pCalcStatement != null) {
            LSFExpressionUnfriendlyPD unfr = pCalcStatement.getExpressionUnfriendlyPD();
            if (unfr != null)
                return unfr.resolveValueParamClasses();

            LSFPropertyExpression expr = pCalcStatement.getPropertyExpression();
            if (expr != null)
                return LSFExClassSet.toEx(LSFPsiImplUtil.resolveValueParamClasses(expr));
        }
        return null;
    }

    public static LSFClassSet finishValueClass(LSFPropDeclaration decl) {
        return LSFExClassSet.fromEx(decl.resolveExValueClass(false));
    }
    
    @Override
    public LSFClassSet resolveValueClass() {
        return finishValueClass(this);
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

    public boolean isPersistentProperty() {
        LSFNonEmptyPropertyOptions options = getNonEmptyPropertyOptions();
        return options != null && !options.getPersistentSettingList().isEmpty();
    }
    
    public boolean isStoredProperty() {
        return isDataStoredProperty() || isPersistentProperty();
    }

    public byte getPropType() {
        if (isAbstract()) {
            return 2;
        } else if (isDataProperty()) {
            return 1;
        } else {
            return 0;
        }
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
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.PROP;
    }

    @Override
    protected LSFPropReference getImplementation(PsiReference ref) {
        LSFOverridePropertyStatement overrideStatement = PsiTreeUtil.getParentOfType((PsiElement) ref, LSFOverridePropertyStatement.class);
        if (overrideStatement != null && ref.equals(overrideStatement.getMappedPropertyClassParamDeclare().getPropertyUsageWrapper().getPropertyUsage()))
           return overrideStatement.getMappedPropertyClassParamDeclare().getPropertyUsageWrapper().getPropertyUsage();
        return null;
    }

    protected Condition<LSFGlobalPropDeclaration> getFindDuplicateColumnsCondition() {
        return new Condition<LSFGlobalPropDeclaration>() {
            @Override
            public boolean value(LSFGlobalPropDeclaration decl) {
                String tableName = getTableName();
                String otherTableName = decl.getTableName();

                if (tableName != null && tableName.equals(otherTableName)) {
                    String columnName = getColumnName();
                    String otherColumnName = decl.getColumnName();

                    return columnName != null && columnName.equals(otherColumnName);
                }
                
                return false;
            }
        };
    }
    
    public boolean resolveDuplicateColumns() {
        CollectionQuery<LSFGlobalPropDeclaration> declarations = new CollectionQuery<>(
                LSFGlobalResolver.findElements(
                        getDeclName(), null, getTypes(), getLSFFile(), getFindDuplicateColumnsCondition(), Finalizer.EMPTY
                )
        );
        return declarations.findAll().size() > 1;
    }

    @Nullable
    @Override
    public String getTableName() {
        return TableNameCache.getInstance(getProject()).getTableNameWithCaching(this);
    }

    @Nullable
    @Override
    public String getTableNameNoCache() {
        if (!isStoredProperty()) {
            return null;
        }
        
        List<LSFClassSet> classesList = resolveParamClasses();
        if (classesList == null) {
            return null;
        }
        
        int paramCount = classesList.size();
        
        if (paramCount == 0) {
            return TABLE_BASE0;
        }

        boolean useBase = false;
        LSFValueClass classes[][] = new LSFValueClass[paramCount][];
        int currentInd[] = new int[paramCount];
        LSFValueClass currentSet[] = new LSFValueClass[paramCount];
        for (int i = 0; i < paramCount; i++) {
            LSFClassSet classSet = classesList.get(i);
            if (classSet == null) {
                useBase = true;
                break;
            } else {
                Collection<LSFValueClass> parents = CustomClassSet.getClassParentsRecursively(classSet.getCommonClass());
                classes[i] = new LSFValueClass[parents.size()];
                int j = 0;
                for (LSFValueClass parent : parents) {
                    classes[i][j++] = parent;
                }
                if (j == 0) {
                    useBase = true;
                    break;
                }
                
                currentSet[i] = classes[i][0];
                currentInd[i] = 0;
            }
        }
        
        if (!useBase) {
            //перебираем возможные классы параметров
            do {
                String tableName = findAppropriateTable(currentSet);
                if (tableName != null) {
                    return tableName;
                }

                int i = paramCount - 1;
                while (i >= 0 && currentInd[i] == classes[i].length - 1) {
                    currentInd[i] = 0;
                    currentSet[i] = classes[i][0];
                    --i;
                }

                if (i < 0) {
                    break;
                }

                currentInd[i]++;
                currentSet[i] = classes[i][currentInd[i]];
            } while (true);
        }

        return TABLE_BASE_PREFIX + paramCount;
    }

    @Nullable
    private String findAppropriateTable(@NotNull LSFValueClass[] currentSet) {
        String names[] = new String[currentSet.length];
        for (int i = 0; i < currentSet.length; i++) {
            names[i] = currentSet[i].getName();
        }

        String key = BaseUtils.toString(names);
        
        Collection<LSFTableDeclaration> tables = TableClassesIndex.getInstance().get(key, getProject(), getLSFFile().getRequireScope());
        for (LSFTableDeclaration table : tables) {
            LSFValueClass[] tableClasses = table.getClasses();

            if (tableClasses.length != currentSet.length) {
                continue;
            }

            boolean fit = true;
            for (int i = 0; i < currentSet.length; ++i) {
                if (tableClasses[i] != currentSet[i]) {
                    fit = false;
                    break;
                }
            }

            if (fit) {
                return table.getNamespaceName() + "_" + table.getName();
            }
        }

        return null;
    }

    @Nullable
    @Override
    public String getColumnName() {
        return ColumnNameCache.getInstance(getProject()).getColumnNameWithCaching(this);
    }

    @Nullable
    @Override
    public String getColumnNameNoCache() {
        if (!isStoredProperty()) {
            return null;
        }

        LSFFile lsfFile = getLSFFile();
        ColumnNamingPolicy columnNamingPolicy = ColumnNamingPolicyCache.getInstance(lsfFile).getColumnNamingPolicyWithCaching(lsfFile);
        return columnNamingPolicy == null ? null : columnNamingPolicy.getColumnName(this);
    }

    @Override
    public Integer getComplexity() {
        return getPropComplexity(this);
    }

    public static Integer getPropComplexity(LSFPropDeclaration prop) {
        return getPropComplexity(prop, new HashSet<LSFPropDeclaration>());
    }
    
    private static Integer getPropComplexity(LSFPropDeclaration prop, Set<LSFPropDeclaration> processed) {
        Integer complexity = 1;
        if (!processed.contains(prop)) {
            processed.add(prop);
            if (prop instanceof LSFGlobalPropDeclaration && !((LSFGlobalPropDeclaration) prop).isPersistentProperty()) {
                Set<LSFActionOrGlobalPropDeclaration> dependencies = PropertyDependenciesCache.getInstance(prop.getProject()).resolveWithCaching((LSFGlobalPropDeclaration)prop);
                for (LSFActionOrGlobalPropDeclaration dependency : dependencies) {
                    complexity += getPropComplexity((LSFGlobalPropDeclaration)dependency, processed);
                }
            }
        }

        return complexity;    
    }

    @Override
    public Icon getIcon() {
        return getIcon(0);
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
    protected void fillImplementationDependencies(LSFActionOrPropReference impRef, Collection<LSFActionOrPropReference> references) {
        LSFOverridePropertyStatement overrideStatement = PsiTreeUtil.getParentOfType(impRef, LSFOverridePropertyStatement.class);
        for (LSFPropertyExpression propertyExpression : overrideStatement.getPropertyExpressionList()) {
            references.addAll(PsiTreeUtil.findChildrenOfType(propertyExpression, LSFPropReference.class));
        }
    }
}
