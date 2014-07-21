package com.lsfusion.lang;

import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;

public class SimpleColumnNamingPolicy extends ColumnNamingPolicy {
    @Override
    public String getColumnName(LSFGlobalPropDeclaration declaration) {
        return declaration.getName();
    }
}
