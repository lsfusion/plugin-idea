package com.lsfusion.lang.psi.declarations;

import com.intellij.openapi.util.Condition;
import com.intellij.util.CollectionQuery;
import com.lsfusion.lang.DBNamingPolicy;
import com.lsfusion.lang.classes.CustomClassSet;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.cache.ColumnNameCache;
import com.lsfusion.lang.psi.cache.DBNamingPolicyCache;
import com.lsfusion.lang.psi.cache.TableNameCache;
import com.lsfusion.lang.psi.indexes.TableClassesIndex;
import com.lsfusion.lang.psi.stubs.PropStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface LSFGlobalPropDeclaration<This extends LSFGlobalPropDeclaration<This,Stub>, Stub extends PropStubElement<Stub, This>> extends LSFActionOrGlobalPropDeclaration<This, Stub>, LSFPropDeclaration {

    String getCaption();

    boolean isAction();
    
    boolean isStoredProperty();

    boolean isPersistentProperty();

    boolean isDataProperty();

    boolean isDataStoredProperty();

    Collection<FullNameStubElementType> getTypes();
    
    default Condition<LSFGlobalPropDeclaration> getFindDuplicateColumnsCondition() {
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

    default boolean resolveDuplicateColumns() {
        CollectionQuery<LSFGlobalPropDeclaration> declarations = new CollectionQuery<LSFGlobalPropDeclaration>(
                LSFGlobalResolver.findElements(
                        getDeclName(), null, getTypes(), getLSFFile(), getFindDuplicateColumnsCondition(), Finalizer.EMPTY
                )
        );
        return declarations.findAll().size() > 1;
    }

    @Nullable
    default String getTableName() {
        return TableNameCache.getInstance(getProject()).getTableNameWithCaching(this);
    }

    @Nullable
    default String getTableNameNoCache() {
        if (!isStoredProperty()) {
            return null;
        }

        DBNamingPolicy dbNamingPolicy = DBNamingPolicy.getInstance(this);

        LSFNonEmptyPropertyOptions options = getNonEmptyPropertyOptions();
        if(options != null) {
            List<LSFTableUsage> tableUsages = options.getTableUsageList();
            if(tableUsages.size() > 0) {
                LSFTableDeclaration table = tableUsages.get(0).resolveDecl();
                if(table != null)
                    return dbNamingPolicy.getTableName(table);
            }
        }

        List<LSFClassSet> classesList = resolveParamClasses();
        if (classesList == null) {
            return null;
        }

        int paramCount = classesList.size();

        boolean useAuto = paramCount == 0;
        if (!useAuto) {
            LSFValueClass classes[][] = new LSFValueClass[paramCount][];
            int currentInd[] = new int[paramCount];
            LSFValueClass currentSet[] = new LSFValueClass[paramCount];
            for (int i = 0; i < paramCount; i++) {
                LSFClassSet classSet = classesList.get(i);
                if (classSet == null) {
                    useAuto = true;
                    break;
                } else {
                    Collection<LSFValueClass> parents = CustomClassSet.getClassParentsRecursively(classSet.getCommonClass());
                    classes[i] = new LSFValueClass[parents.size()];
                    int j = 0;
                    for (LSFValueClass parent : parents) {
                        classes[i][j++] = parent;
                    }
                    if (j == 0) {
                        useAuto = true;
                        break;
                    }

                    currentSet[i] = classes[i][0];
                    currentInd[i] = 0;
                }
            }


            if (!useAuto) {
                //перебираем возможные классы параметров
                do {
                    LSFTableDeclaration table = findAppropriateTable(currentSet);
                    if (table != null) {
                        return dbNamingPolicy.getTableName(table);
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
        }
        return dbNamingPolicy.createAutoTableDBName(classesList);
    }

    default LSFTableDeclaration findAppropriateTable(@NotNull LSFValueClass[] currentSet) {
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
                return table;
            }
        }

        return null;
    }

    @Nullable
    default String getColumnName() {
        return ColumnNameCache.getInstance(getProject()).getColumnNameWithCaching(this);
    }

    @Nullable
    default String getColumnNameNoCache() {
        if (!isStoredProperty()) {
            return null;
        }

        LSFFile lsfFile = getLSFFile();
        DBNamingPolicy dbNamingPolicy = DBNamingPolicyCache.getInstance(lsfFile).getDBNamingPolicyWithCaching(lsfFile);
        return dbNamingPolicy == null ? null : dbNamingPolicy.getColumnName(this);
    }

    default byte getPropType() {
        if (isAbstract()) {
            return 2;
        } else if (isDataProperty()) {
            return 1;
        } else {
            return 0;
        }
    }
}
