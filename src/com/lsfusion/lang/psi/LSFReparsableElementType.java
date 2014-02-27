
package com.lsfusion.psi;

import com.intellij.psi.tree.IElementType;
import com.lsfusion.LSFLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class LSFReparsableElementType extends IElementType {
    public LSFReparsableElementType(@NotNull @NonNls String debugName) {
        super(debugName, LSFLanguage.INSTANCE);
    }
}