package com.lsfusion.refactoring;

import com.intellij.openapi.vfs.VirtualFile;
import com.lsfusion.lang.psi.LSFDataPropertyDefinition;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;

public class PropertyMigration {
    private final VirtualFile declarationFile;
    private final String migrationString;

    PropertyMigration(LSFGlobalPropDeclaration decl, String oldName, String newName) {
        String signatureQualifier = computeSignatureQualifier(decl);
        String oldFullName = decl.getNamespaceName() + "." + oldName + signatureQualifier;
        String newFullName = decl.getNamespaceName() + "." + newName + signatureQualifier;
        this.migrationString = oldFullName + " -> " + newFullName;
        this.declarationFile = decl.getContainingFile().getVirtualFile();
    }

    private String computeSignatureQualifier(LSFGlobalPropDeclaration decl) {
        LSFDataPropertyDefinition dataProp = decl.getDataPropertyDefinition();
        
        assert dataProp != null;

        String signature = "";
        for (String className : dataProp.getMigrationClassNames()) {
            if (!signature.isEmpty()) {
                signature += ",";
            }
            signature += className;
        }
        
        return "[" + signature + "]";
    }

    public VirtualFile getDeclarationFile() {
        return declarationFile;
    }

    public String getMigrationString() {
        return migrationString;
    }
}
