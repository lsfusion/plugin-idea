package com.lsfusion.refactoring;

import com.lsfusion.lang.psi.declarations.LSFStaticObjectDeclaration;

import static com.lsfusion.refactoring.CompoundNameUtils.createStaticObjectName;

public class StaticObjectMigration extends ElementMigration {
    public StaticObjectMigration(LSFStaticObjectDeclaration decl, String classNamespace, String className, String oldName, String newName) {
        this(decl, classNamespace, className, className, oldName, newName);
    }

    public StaticObjectMigration(LSFStaticObjectDeclaration decl, String classNamespace, String oldClassName, String newClassName, String oldName, String newName) {
        super(decl, createStaticObjectName(classNamespace, oldClassName, oldName), createStaticObjectName(classNamespace, newClassName, newName));
    }
    
    @Override
    public String getPrefix() {
        return "OBJECT";
    }
    
}
