package com.lsfusion.refactoring;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;

import java.util.List;

public class PropertyMigration extends ElementMigration {

    private final boolean isStored;
    
    public static PropertyMigration create(LSFGlobalPropDeclaration decl, String oldName, String newName) {
        String namespace = decl.getNamespaceName();
        
        List<LSFClassSet> paramsClasses = decl.resolveParamClasses();
        String oldFullName = PropertyCanonicalNameUtils.createName(namespace, oldName, paramsClasses);
        String newFullName = PropertyCanonicalNameUtils.createName(namespace, newName, paramsClasses);
        
        return new PropertyMigration(decl, oldFullName, newFullName, decl.isStoredProperty());
    }

    public static PropertyMigration create(LSFGlobalPropDeclaration decl, List<LSFClassSet> oldClasses, List<LSFClassSet> newClasses) {
        String namespace = decl.getNamespaceName();
        
        String name = decl.getDeclName();
        String oldFullName = PropertyCanonicalNameUtils.createName(namespace, name, oldClasses);
        String newFullName = PropertyCanonicalNameUtils.createName(namespace, name, newClasses);

        return new PropertyMigration(decl, oldFullName, newFullName, decl.isStoredProperty());
    }

    public PropertyMigration(LSFGlobalPropDeclaration decl, String oldName, String newName, boolean isStored) {
        super(decl, oldName, newName);
        
        this.isStored = isStored;
    }

    @Override
    public String getPrefix() {
        return (isStored ? "STORED " : "") + "PROPERTY";
    }
}
