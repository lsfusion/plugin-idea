package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.LSFTreeGroupDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFTreeGroupReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;

public abstract class LSFTreeGroupReferenceImpl extends LSFFormElementReferenceImpl<LSFTreeGroupDeclaration> implements LSFTreeGroupReference {
    protected LSFTreeGroupReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected Function<LSFFormExtend, Collection<LSFTreeGroupDeclaration>> getElementsCollector() {
        return LSFFormExtend::getTreeGroupDecls;
    }

    @Override
    protected boolean isDeclarationType(PsiElement element) {
        return element instanceof LSFTreeGroupDeclaration;
    }
}

