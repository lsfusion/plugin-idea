package com.lsfusion.refactoring;

import com.lsfusion.lang.psi.declarations.LSFNavigatorElementDeclaration;

import static com.lsfusion.refactoring.CompoundNameUtils.createName;

public class NavigatorElementMigration extends ElementMigration {
    public NavigatorElementMigration(LSFNavigatorElementDeclaration decl, String oldName, String newName) {
        super(decl, createName(decl.getNamespaceName(), oldName), createName(decl.getNamespaceName(), newName));
    }

    @Override
    public String getPrefix() {
        return "NAVIGATOR";
    }
}
