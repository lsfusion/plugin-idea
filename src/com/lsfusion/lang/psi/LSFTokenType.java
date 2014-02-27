package com.lsfusion.psi;

import com.intellij.psi.tree.IElementType;
import com.lsfusion.LSFLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class LSFTokenType extends IElementType {
    public LSFTokenType(@NotNull @NonNls String debugName) {
        super(debugName, LSFLanguage.INSTANCE);
    }

    public String tokenText() {
        return super.toString();
    }

    @Override
    public String toString() {
        return "LSFTokenType." + super.toString();
    }
}