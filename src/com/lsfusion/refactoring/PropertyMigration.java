package com.lsfusion.refactoring;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;

import java.util.List;

public class PropertyMigration extends ElementMigration {

    private final boolean isStored;
    private final boolean isAction;

    public PropertyMigration(LSFActionOrGlobalPropDeclaration<?, ?> decl, String oldName, String newName) {
        this(decl, decl.resolveParamClasses(), decl.resolveParamClasses(), oldName, newName);
    }

    private PropertyMigration(LSFActionOrGlobalPropDeclaration<?, ?> decl, List<LSFClassSet> oldClasses, List<LSFClassSet> newClasses, String oldName, String newName) {
        this(decl, PropertyCanonicalNameUtils.createName(decl.getNamespaceName(), oldName, oldClasses),
                    PropertyCanonicalNameUtils.createName(decl.getNamespaceName(), newName, newClasses), true);
    }

    public PropertyMigration(LSFActionOrGlobalPropDeclaration<?, ?> decl, String oldCanonicalName, String newCanonicalName, boolean unused) {
        super(decl, oldCanonicalName, newCanonicalName);

        this.isAction = decl.isAction();
        this.isStored = decl instanceof LSFGlobalPropDeclaration && ((LSFGlobalPropDeclaration<?, ?>) decl).isStoredProperty();
    }

    @Override
    public String getPrefix() {
        return isAction ? "ACTION" : ((isStored ? "STORED " : "") + "PROPERTY");
    }
}
