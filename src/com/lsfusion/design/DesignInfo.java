package com.lsfusion.design;

import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;

public class DesignInfo {
    public final LSFFormDeclaration formDecl;

    public DesignInfo(LSFFormDeclaration formDecl) {
        this.formDecl = formDecl;
    }

    public String getFormCaption() {
        return formDecl.getCaption();
    }

    public String getFormSID() {
        return formDecl.getGlobalName();
    }
}
