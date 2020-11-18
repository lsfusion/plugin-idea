package com.lsfusion.refactoring;

import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;

import static com.lsfusion.refactoring.CompoundNameUtils.createName;

public class TableMigration extends ElementMigration  {
    public TableMigration(LSFTableDeclaration decl, String oldName, String newName) {
        super(decl, createName(decl.getNamespaceName(), oldName), createName(decl.getNamespaceName(), newName));
    }

    @Override
    public String getPrefix() {
        return "TABLE";
    }
}
