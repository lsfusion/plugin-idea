package com.lsfusion.refactoring;

import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;

public class ClassMigration extends ElementMigration {
    public static ClassMigration create(LSFClassDeclaration decl, String oldName, String newName) {
        String namespace = decl.getNamespaceName();

        String oldFullName = CompoundNameUtils.createName(namespace, oldName);
        String newFullName = CompoundNameUtils.createName(namespace, newName);

        return new ClassMigration(decl, oldFullName, newFullName);
    }

    private ClassMigration(LSFClassDeclaration decl, String oldName, String newName) {
        super(decl, oldName, newName);
    }

    @Override
    public String getPrefix() {
        return "CLASS";
    }
    
}
