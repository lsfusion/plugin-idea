package com.lsfusion.refactoring;

import com.intellij.openapi.vfs.VirtualFile;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;

public abstract class ElementMigration {
    protected String oldName;
    protected String newName;
    protected final LSFDeclaration decl;
    
    public ElementMigration(LSFDeclaration decl, String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
        this.decl = decl;
    }

    public VirtualFile getDeclarationFile() {
        return decl.getContainingFile().getVirtualFile();
    }

    public String getMigrationString() {
        return getPrefix() + " " + oldName + " -> " + newName;
    }

    protected abstract String getPrefix();
}
