
package com.lsfusion.migration.lang.psi;

import com.intellij.psi.tree.IElementType;

public class MigrationElementTypeFactory {
    public static IElementType create(String ID) {
        return new MigrationElementType(ID);
    }
}