package com.lsfusion.lang;

import com.lsfusion.lang.classes.*;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class NamespaceNameColumnNamingPolicy extends ColumnNamingPolicy {
    @Override
    public String getColumnName(LSFGlobalPropDeclaration declaration) {
        return declaration.getNamespaceName() + "_" + declaration.getName();
    }
}
