package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.declarations.LSFComponentDeclaration;
import com.lsfusion.lang.psi.extend.LSFDesign;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

public abstract class LSFComponentDeclarationImpl extends LSFDesignElementDeclarationImpl<LSFComponentDeclaration> implements LSFComponentDeclaration {
    protected LSFComponentDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected abstract LSFSimpleName getSimpleName();

    public static Function<LSFDesign, Collection<LSFComponentDeclaration>> getProcessor() {
        return LSFDesign::getComponentDecls;
    }

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getSimpleName();
    }
}
