package com.lsfusion.refactoring;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;

import java.util.List;

public class PropertyMigration extends ElementMigration {

    private final boolean isStored;
    private final boolean isAction;
    
    public static PropertyMigration create(LSFActionOrGlobalPropDeclaration<?, ?> decl, String oldName, String newName) {
        String namespace = decl.getNamespaceName();
        
        List<LSFClassSet> paramsClasses = decl.resolveParamClasses();
        String oldFullName = PropertyCanonicalNameUtils.createName(namespace, oldName, paramsClasses);
        String newFullName = PropertyCanonicalNameUtils.createName(namespace, newName, paramsClasses);
        
        return new PropertyMigration(decl, oldFullName, newFullName);
    }

    public static PropertyMigration create(LSFActionOrGlobalPropDeclaration<?, ?> decl, List<LSFClassSet> oldClasses, List<LSFClassSet> newClasses) {
        String namespace = decl.getNamespaceName();
        
        String name = decl.getDeclName();
        String oldFullName = PropertyCanonicalNameUtils.createName(namespace, name, oldClasses);
        String newFullName = PropertyCanonicalNameUtils.createName(namespace, name, newClasses);

        return new PropertyMigration(decl, oldFullName, newFullName);
    }

    public static PropertyMigration createUsingCanonicalNames(LSFActionOrGlobalPropDeclaration<?, ?> decl, String oldCanonicalName, String newCanonicalName) {
        return new PropertyMigration(decl, oldCanonicalName, newCanonicalName);    
    }
    
    private PropertyMigration(LSFActionOrGlobalPropDeclaration<?, ?> decl, String oldCanonicalName, String newCanonicalName) {
        super(decl, oldCanonicalName, newCanonicalName);
        
        this.isAction = decl.isAction(); 
        this.isStored = decl instanceof LSFGlobalPropDeclaration && ((LSFGlobalPropDeclaration<?, ?>) decl).isStoredProperty();
    }

    @Override
    public String getPrefix() {
        return isAction ? "ACTION" : ((isStored ? "STORED " : "") + "PROPERTY");
    }
}
