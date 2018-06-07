package com.lsfusion.refactoring;

import com.lsfusion.lang.psi.declarations.LSFStaticObjectDeclaration;

public class StaticObjectMigration extends ElementMigration {
    public static StaticObjectMigration create(LSFStaticObjectDeclaration decl, String classNamespace, String className, String oldName, String newName) {
        String oldFullName = CompoundNameUtils.createStaticObjectName(classNamespace, className, oldName);
        String newFullName = CompoundNameUtils.createStaticObjectName(classNamespace, className, newName);

        return new StaticObjectMigration(decl, oldFullName, newFullName);
    }

    private StaticObjectMigration(LSFStaticObjectDeclaration decl, String oldFullName, String newFullName) {
        super(decl, oldFullName, newFullName);
    }

    @Override
    public String getPrefix() {
        return "OBJECT";
    }
    
}
