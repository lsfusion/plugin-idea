package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.declarations.LSFFilterGroupDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public abstract class LSFFilterGroupDeclarationImpl extends LSFFormElementDeclarationImpl implements LSFFilterGroupDeclaration {

    protected LSFFilterGroupDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    public abstract LSFSimpleName getSimpleName();

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.FILTER;
    }

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getSimpleName();
    }

    public static Processor<LSFFilterGroupDeclaration> getProcessor() {
        return new Processor<LSFFilterGroupDeclaration>() {
            public Collection<LSFFilterGroupDeclaration> process(LSFFormExtend formExtend) {
                return formExtend.getFilterGroupDecls();
            }
        };
    }
}
