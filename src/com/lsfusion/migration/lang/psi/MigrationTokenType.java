package com.lsfusion.migration.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.lsfusion.lang.LSFLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class MigrationTokenType extends IElementType {
    public MigrationTokenType(@NotNull @NonNls String debugName) {
        super(debugName, LSFLanguage.INSTANCE);
    }

    public String tokenText() {
        return super.toString();
    }

    @Override
    public String toString() {
        return "MigrationTokenType." + super.toString();
    }
}