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
import com.lsfusion.lang.psi.cache.ColumnNameCache;
import com.lsfusion.lang.psi.cache.ParamClassesCache;
import com.lsfusion.lang.psi.cache.TableNameCache;
import com.lsfusion.lang.psi.cache.ValueClassCache;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;
import com.lsfusion.lang.psi.indexes.TableClassesIndex;
import com.lsfusion.lang.psi.stubs.PropStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import com.lsfusion.util.BaseUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

public abstract class LSFGlobalPropDeclarationImpl extends LSFFullNameDeclarationImpl<LSFGlobalPropDeclaration, PropStubElement> implements LSFGlobalPropDeclaration {

    public static final String TABLE_BASE_PREFIX = "base";
    public static final String TABLE_BASE0 = TABLE_BASE_PREFIX + 0;

    public LSFGlobalPropDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFGlobalPropDeclarationImpl(@NotNull PropStubElement propStubElement, @NotNull IStubElementType nodeType) {
        super(propStubElement, nodeType);
    }

    @Override
    public List<String> resolveParamNames() {
        LSFPropertyDeclaration decl = getPropertyDeclaration();
        LSFClassParamDeclareList declList = decl.getClassParamDeclareList();
        List<LSFExprParamDeclaration> params = null;
        if(declList!=null)
            params = BaseUtils.<List<LSFExprParamDeclaration>>immutableCast(LSFPsiImplUtil.resolveParams(declList));
        else {
            LSFPropertyExpression pe = getPropertyExpression();
            if(pe!=null)
                params = LSFPsiImplUtil.resolveParams(pe);
        }
        if(params != null) {
            List<String> result = new ArrayList<String>();
            for(LSFExprParamDeclaration param : params)
                result.add(param.getDeclName());
            return result;
        }

        return null;
    }

    public abstract LSFPropertyDeclaration getPropertyDeclaration();

    @Nullable
    public abstract LSFExpressionUnfriendlyPD getExpressionUnfriendlyPD();

    @Nullable
    public abstract LSFPropertyExpression getPropertyExpression();

    @Nullable
    public abstract LSFPropertyOptions getPropertyOptions();

    @Override
    public boolean isAbstract() {
        LSFExpressionUnfriendlyPD unfriend = getExpressionUnfriendlyPD();
        if(unfriend != null) {
            LSFContextIndependentPD contextIndID = unfriend.getContextIndependentPD();
            if(contextIndID!=null)
                return contextIndID.getAbstractPropertyDefinition() != null || contextIndID.getAbstractActionPropertyDefinition() != null;
        }
        return false;
    }

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
    public LSFExClassSet resolveExValueClass(boolean infer) {
        return ValueClassCache.getInstance(getProject()).resolveValueClassWithCaching(this, infer);
    }

    @Override
    public LSFExClassSet resolveExValueClassNoCache(boolean infer) {
        LSFExpressionUnfriendlyPD unfr = getExpressionUnfriendlyPD();
        if (unfr != null)
            return unfr.resolveUnfriendValueClass(infer);

        LSFPropertyExpression expr = getPropertyExpression();
        if (expr != null)
            return expr.resolveValueClass(infer);

        return null;
    }

    @Nullable
    private List<LSFExClassSet> resolveValueParamClasses() {
        LSFExpressionUnfriendlyPD unfr = getExpressionUnfriendlyPD();
        if (unfr != null)
            return unfr.resolveValueParamClasses();

        LSFPropertyExpression expr = getPropertyExpression();
        if (expr != null)
            return LSFExClassSet.toEx(LSFPsiImplUtil.resolveValueParamClasses(expr));

        return null;

    }

    @Override
    @Nullable
    public List<LSFExClassSet> resolveExParamClasses() {
        return ParamClassesCache.getInstance(getProject()).resolveParamClassesWithCaching(this);
    }

    @Override
    public List<LSFExClassSet> resolveExParamClassesNoCache() {
        LSFPropertyDeclaration decl = getPropertyDeclaration();
        LSFClassParamDeclareList cpd = decl.getClassParamDeclareList();
        List<LSFExClassSet> declareClasses = null;
        if (cpd != null) {
            declareClasses = LSFExClassSet.toEx(LSFPsiImplUtil.resolveClass(cpd));
            if (LSFPsiImplUtil.allClassesDeclared(declareClasses)) // оптимизация
                return declareClasses;
        }

        List<LSFExClassSet> valueClasses = resolveValueParamClasses();
        if (valueClasses == null)
            return declareClasses;

        if (declareClasses == null)
            return valueClasses;

        List<LSFExClassSet> mixed = new ArrayList<LSFExClassSet>(declareClasses);
        for (int i = 0, size = declareClasses.size(); i < size; i++) {
            if (i >= valueClasses.size())
                break;

            if (declareClasses.get(i) == null)
                mixed.set(i, valueClasses.get(i));
        }
        return Collections.unmodifiableList(mixed);
    }

    public static List<LSFClassSet> finishParamClasses(LSFPropDeclaration decl) {
        return LSFExClassSet.fromEx(decl.resolveExParamClasses());
    }

    public static LSFClassSet finishValueClass(LSFPropDeclaration decl) {
        return LSFExClassSet.fromEx(decl.resolveExValueClass(false));
    }
    
    @Override
    public List<LSFClassSet> resolveParamClasses() {
        return finishParamClasses(this);
    }

    @Override
    public LSFClassSet resolveValueClass() {
        return finishValueClass(this);
    }

    @Override
    @Nullable
    public List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass) {

        List<LSFExClassSet> resultClasses = resolveExParamClasses();
        if (resultClasses == null)
            return null;
        
        resultClasses = new ArrayList<LSFExClassSet>(resultClasses);

        //        LSFActionPropertyDefinition action = sourceStatement.getActionPropertyDefinition();
//        return 

        List<LSFExprParamDeclaration> params = null;
        LSFPropertyDeclaration decl = getPropertyDeclaration();
        LSFClassParamDeclareList cpd = decl.getClassParamDeclareList();
        if (cpd != null)
            params = BaseUtils.<List<LSFExprParamDeclaration>>immutableCast(LSFPsiImplUtil.resolveParams(cpd));

        InferExResult inferredClasses = null;
        LSFExpressionUnfriendlyPD unfriendlyPD = getExpressionUnfriendlyPD();
        if (unfriendlyPD != null) {
            LSFActionPropertyDefinition actionDef = unfriendlyPD.getActionPropertyDefinition();
            if (actionDef != null) {
                if (params == null)
                    params = BaseUtils.<List<LSFExprParamDeclaration>>immutableCast(LSFPsiImplUtil.resolveParams(actionDef.getExprParameterUsageList()));
                if (params != null) // может быть action unfriendly
                    inferredClasses = LSFPsiImplUtil.inferActionParamClasses(actionDef.getActionPropertyDefinitionBody(), new HashSet<LSFExprParamDeclaration>(params)).finishEx();
            } else {
                LSFContextIndependentPD contextIndependentPD = unfriendlyPD.getContextIndependentPD();

                assert contextIndependentPD != null;

                PsiElement element = contextIndependentPD.getChildren()[0]; // лень создавать отдельный параметр или интерфейс
                if (element instanceof LSFGroupPropertyDefinition) {
                    List<LSFExClassSet> inferredValueClasses = LSFPsiImplUtil.inferGroupValueParamClasses((LSFGroupPropertyDefinition) element);
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

                inferredClasses = LSFPsiImplUtil.inferParamClasses(expr, valueClass).finishEx();
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

    public LSFDataPropertyDefinition getDataPropertyDefinition() {
        LSFExpressionUnfriendlyPD expressionUnfriendlyPD = getExpressionUnfriendlyPD();
        if (expressionUnfriendlyPD != null) {
            LSFContextIndependentPD contextIndependentPD = expressionUnfriendlyPD.getContextIndependentPD();
            if (contextIndependentPD != null) {
                return contextIndependentPD.getDataPropertyDefinition();
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
    
    public boolean isStoredProperty() {
        if (isDataStoredProperty()) {
            return true;
        }
        
        LSFPropertyOptions options = getPropertyOptions();
        return options != null && !options.getPersistentSettingList().isEmpty();
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
            LSFClassSet valueClass = resolveValueClass();
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
        LSFId nameIdentifier = getNameIdentifier();
        if (nameIdentifier == null) {
            return PsiElement.EMPTY_ARRAY;
        }
        Collection<PsiReference> refs = ReferencesSearch.search(nameIdentifier, GlobalSearchScope.allScope(getProject())).findAll();

        List<PsiElement> result = new ArrayList<PsiElement>();
        for (PsiReference ref : refs) {
            LSFOverrideStatement overrideStatement = PsiTreeUtil.getParentOfType((PsiElement) ref, LSFOverrideStatement.class);
            if (overrideStatement != null && ref.equals(overrideStatement.getMappedPropertyClassParamDeclare().getPropertyUsageWrapper().getPropertyUsage())) {
                result.add(overrideStatement.getMappedPropertyClassParamDeclare().getPropertyUsageWrapper());    
            }
        }
        return result.toArray(new PsiElement[result.size()]);
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
        CollectionQuery<LSFGlobalPropDeclaration> declarations = new CollectionQuery<LSFGlobalPropDeclaration>(
                LSFGlobalResolver.findElements(
                        getDeclName(), null, getLSFFile(), getTypes(), getFindDuplicateColumnsCondition(), Finalizer.EMPTY
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
        return ColumnNamingPolicy.getInstance(getProject()).getColumnName(this);
    }
}
