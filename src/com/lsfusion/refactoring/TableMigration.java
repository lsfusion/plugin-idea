package com.lsfusion.refactoring;

import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;

public class TableMigration extends ElementMigration  {
    public static TableMigration create(LSFTableDeclaration decl, String oldName, String newName) {
        String namespace = decl.getNamespaceName();

        String oldFullName = CompoundNameUtils.createName(namespace, oldName);
        String newFullName = CompoundNameUtils.createName(namespace, newName);

        return new TableMigration(decl, oldFullName, newFullName);
    }
        
    private TableMigration(LSFTableDeclaration decl, String oldName, String newName) {
        super(decl, oldName, newName);
    }

    @Override
    public String getPrefix() {
        return "TABLE";
    }
}
