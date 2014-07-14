package com.lsfusion.migration.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.lsfusion.migration.lang.MigrationLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class MigrationElementType extends IElementType {
    public MigrationElementType(@NotNull @NonNls String debugName) {
        super(debugName, MigrationLanguage.INSTANCE);
    }
}
