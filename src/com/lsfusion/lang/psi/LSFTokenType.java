package com.lsfusion.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.LSFLexer;
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
        String string = LSFLexer.getTokenDebugName(this);
        return string != null ? string : super.toString();
    }
}