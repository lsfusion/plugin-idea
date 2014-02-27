
package com.lsfusion.psi;

import com.intellij.psi.tree.IElementType;
import com.lsfusion.LSFLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class LSFElementType extends IElementType {
    public LSFElementType(@NotNull @NonNls String debugName) {
        super(debugName, LSFLanguage.INSTANCE);
    }
}