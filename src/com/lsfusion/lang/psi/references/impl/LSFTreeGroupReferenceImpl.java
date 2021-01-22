package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.LSFTreeGroupDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGroupObjectDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFTreeGroupReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class LSFTreeGroupReferenceImpl extends LSFFormElementReferenceImpl<LSFTreeGroupDeclaration> implements LSFTreeGroupReference {
    protected LSFTreeGroupReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected FormExtendProcessor<LSFTreeGroupDeclaration> getElementsCollector() {
        return LSFFormExtend::getTreeGroupDecls;
    }
}

