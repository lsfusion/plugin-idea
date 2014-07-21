package com.lsfusion.lang;

import com.intellij.openapi.project.Project;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;

public abstract class ColumnNamingPolicy {
    public static final ColumnNamingPolicy SIMPLE = new SimpleColumnNamingPolicy();
    public static final ColumnNamingPolicy DEFAULT = new DefaultColumnNamingPolicy();

    public static ColumnNamingPolicy getInstance(Project project) {
        //todo: make configurable
        return DEFAULT;
    }

    public abstract String getColumnName(LSFGlobalPropDeclaration declaration);
}
