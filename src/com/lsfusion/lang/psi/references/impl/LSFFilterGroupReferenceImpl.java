package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFFilterGroupDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFFilterGroupReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;

public abstract class LSFFilterGroupReferenceImpl extends LSFFormElementReferenceImpl<LSFFilterGroupDeclaration> implements LSFFilterGroupReference {
    protected LSFFilterGroupReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected Function<LSFFormExtend, Collection<LSFFilterGroupDeclaration>> getElementsCollector() {
        return LSFFormExtend::getFilterGroupDecls;
    }

    @Override
    protected boolean isDeclarationType(PsiElement element) {
        return element instanceof LSFFilterGroupDeclaration;
    }
}
