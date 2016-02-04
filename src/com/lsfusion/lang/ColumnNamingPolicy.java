package com.lsfusion.lang;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.util.BaseUtils;
import com.lsfusion.util.LSFFileUtils;

public abstract class ColumnNamingPolicy {
    public static final ColumnNamingPolicy SIMPLE = new SimpleColumnNamingPolicy();
    public static final ColumnNamingPolicy DEFAULT = new DefaultColumnNamingPolicy();

    public static ColumnNamingPolicy getInstance(PsiElement element) {
        ColumnNamingPolicy policy = DEFAULT;
        String policyName = LSFFileUtils.getDBNamingPolicy(element);
        if (!BaseUtils.isRedundantString(policyName)) {
            switch (policyName) {
                case "lsfusion.server.logics.DefaultDBNamePolicy":
                    policy = new DefaultColumnNamingPolicy();
                    break;
                case "lsfusion.server.logics.SimpleDBNamePolicy":
                    policy = new SimpleColumnNamingPolicy();
                    break;
                case "lsfusion.server.logics.NamespaceNameDBNamePolicy":
                    policy = new NamespaceNameColumnNamingPolicy();
                    break;
            }
        }
        return policy;
    }

    public abstract String getColumnName(LSFGlobalPropDeclaration declaration);
}
