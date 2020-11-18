package com.lsfusion.refactoring;

import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;

import static com.lsfusion.refactoring.CompoundNameUtils.createName;

public class ClassMigration extends ElementMigration {
    public ClassMigration(LSFClassDeclaration decl, String oldName, String newName) {
        super(decl, createName(decl.getNamespaceName(), oldName), createName(decl.getNamespaceName(), newName));
    }

    @Override
    public String getPrefix() {
        return "CLASS";
    }
}
