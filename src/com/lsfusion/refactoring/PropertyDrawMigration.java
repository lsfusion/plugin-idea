package com.lsfusion.refactoring;

import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFPsiImplUtil;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;

public class PropertyDrawMigration extends ElementMigration {
    public PropertyDrawMigration(LSFPropertyDrawDeclaration decl, String newName) {
        super(decl, "", newName);
        
        LSFSimpleName aliasName = decl.getSimpleName();
        if (aliasName == null) { // значит внутри rename'ся, нужно добавить postfix
            LSFId nameDecl = LSFRenameFullNameProcessor.getDeclPropName(decl);
            assert nameDecl != null;

            String objectsPostfix = LSFPsiImplUtil.getObjectUsageString(decl.getObjectUsageList());
            this.oldName = nameDecl.getName() + objectsPostfix;
            this.newName += objectsPostfix;
        } else {
            this.oldName = aliasName.getName();
        }

        LSFFormDeclaration formDecl = decl.resolveFormDecl();
        if (formDecl != null) {
            String formName = formDecl.getCanonicalName();
            this.oldName = formName + "." + this.oldName;
            this.newName = formName + "." + this.newName;
        }
    }

    @Override
    public String getPrefix() {
        return "FORM PROPERTY";
    }
}
