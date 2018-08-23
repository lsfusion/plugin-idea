package com.lsfusion.refactoring;

import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFNavigatorElementDeclaration;

public class NavigatorElementMigration extends ElementMigration {
    public NavigatorElementMigration(LSFDeclaration decl, String oldName, String newName) {
        super(decl, oldName, newName);
    }

    public static NavigatorElementMigration create(LSFNavigatorElementDeclaration decl, String oldName, String newName) {
        String namespace = decl.getNamespaceName();

        String oldFullName = CompoundNameUtils.createName(namespace, oldName);
        String newFullName = CompoundNameUtils.createName(namespace, newName);

        return new NavigatorElementMigration(decl, oldFullName, newFullName);
    }
    
    @Override
    public String getPrefix() {
        return "NAVIGATOR";
    }
}
