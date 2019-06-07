package com.lsfusion.lang;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;
import com.lsfusion.util.LSFFileUtils;

import java.util.List;

public abstract class DBNamingPolicy {
    public static DBNamingPolicy getInstance(PsiElement element) {
        String policyName = LSFFileUtils.getDBNamingPolicy(element);
        int maxIdSize = LSFFileUtils.getDBMaxIdSize(element);
        
        if (policyName != null) {
            switch (policyName) {
                case "lsfusion.server.physics.dev.id.name.ShortDBNamingPolicy":
                    return new ShortDBNamingPolicy(maxIdSize);
                case "lsfusion.server.physics.dev.id.name.NamespaceDBNamingPolicy":
                    return new NamespaceDBNamingPolicy(maxIdSize);
            }
        }
        return new FullDBNamingPolicy(maxIdSize);
    }

    public abstract String createActionOrPropertyDBName(String namespaceName, String name, List<LSFClassSet> signature);

    public abstract String createTableDBName(String namespaceName, String name);

    public abstract String createAutoTableDBName(List<LSFClassSet> classes);

    public abstract String transformActionOrPropertyCNToDBName(String canonicalName);

    public abstract String transformTableCNToDBName(String canonicalName);
    
    public final String getColumnName(LSFGlobalPropDeclaration declaration) {
        return createActionOrPropertyDBName(declaration.getNamespaceName(), declaration.getName(), declaration.resolveParamClasses());
    }
    
    public final String getTableName(LSFTableDeclaration declaration) {
        return createTableDBName(declaration.getNamespaceName(), declaration.getName());
    }
}
