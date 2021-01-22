package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.declarations.LSFFilterGroupDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFFilterGroupReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class LSFFilterGroupReferenceImpl extends LSFFormElementReferenceImpl<LSFFilterGroupDeclaration> implements LSFFilterGroupReference {
    protected LSFFilterGroupReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected FormExtendProcessor<LSFFilterGroupDeclaration> getElementsCollector() {
        return LSFFormExtend::getFilterGroupDecls;
    }
}
