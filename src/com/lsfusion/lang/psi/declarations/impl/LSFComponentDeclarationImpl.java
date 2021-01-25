package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.declarations.LSFComponentDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import com.lsfusion.lang.psi.extend.LSFDesign;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.impl.LSFDesignElementReferenceImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class LSFComponentDeclarationImpl extends LSFDesignElementDeclarationImpl<LSFComponentDeclaration> implements LSFComponentDeclaration {
    protected LSFComponentDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected abstract LSFSimpleName getSimpleName();

    public static LSFDesignElementReferenceImpl.DesignProcessor<LSFComponentDeclaration> getProcessor() {
        return LSFDesign::getComponentDecls;
    }

    @Nullable
    @Override
    public LSFId getNameIdentifier() {
        return getSimpleName();
    }
}
