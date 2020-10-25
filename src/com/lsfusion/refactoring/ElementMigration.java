package com.lsfusion.refactoring;

import com.intellij.openapi.vfs.VirtualFile;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;

public abstract class ElementMigration {

    private final VirtualFile declarationFile;
    private final String migrationString;

    public ElementMigration(LSFDeclaration decl, String oldName, String newName) {
        this.migrationString = oldName + " -> " + newName;
        this.declarationFile = decl.getContainingFile().getVirtualFile();
    }

    public VirtualFile getDeclarationFile() {
        return declarationFile;
    }

    public String getMigrationString() {
        return migrationString;
    }

    public abstract String getPrefix();
}
