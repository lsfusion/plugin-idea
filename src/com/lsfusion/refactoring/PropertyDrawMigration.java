package com.lsfusion.refactoring;

import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFPsiImplUtil;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import org.jetbrains.annotations.Nullable;

public class PropertyDrawMigration extends ElementMigration {

    @Nullable
    public static PropertyDrawMigration create(LSFPropertyDrawDeclaration decl, String newName) {
        String oldName;
        LSFSimpleName aliasName = decl.getSimpleName();
        if(aliasName == null) { // значит внутри rename'ся, нужно добавить postfix
            LSFId nameDecl = LSFRenameFullNameProcessor.getDeclPropName(decl);
            if(nameDecl == null)
                return null;
            
            String objectsPostfix = LSFPsiImplUtil.getObjectUsageString(decl.getObjectUsageList());
            oldName = nameDecl.getName() + objectsPostfix;
            newName += objectsPostfix;
        } else {
            oldName = aliasName.getName();
        }        

        LSFFormDeclaration formDecl = decl.resolveFormDecl();
        if(formDecl != null) {
            String formName = formDecl.getCanonicalName();
            oldName = formName + "." + oldName;
            newName = formName + "." + newName;
        }

        return new PropertyDrawMigration(decl, oldName, newName);
    }

    private PropertyDrawMigration(LSFPropertyDrawDeclaration decl, String oldName, String newName) {
        super(decl, oldName, newName);
    }

    @Override
    public String getPrefix() {
        return "FORM PROPERTY";
    }
}
