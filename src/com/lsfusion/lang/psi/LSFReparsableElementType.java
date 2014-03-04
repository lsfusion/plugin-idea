
package com.lsfusion.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.ICompositeElementType;
import com.intellij.psi.tree.IErrorCounterReparseableElementType;
import com.lsfusion.lang.LSFLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class LSFReparsableElementType extends IErrorCounterReparseableElementType implements ICompositeElementType {
    public LSFReparsableElementType(@NotNull @NonNls String debugName) {
        super(debugName, LSFLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public ASTNode createCompositeNode() {
        return createNode(null);
    }

    @Override
    @NotNull
    public abstract ASTNode createNode(final CharSequence text);
}