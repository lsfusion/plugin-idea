package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFGroupObjectDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFGroupObjectReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class LSFGroupObjectReferenceImpl extends LSFFormElementReferenceImpl<LSFGroupObjectDeclaration> implements LSFGroupObjectReference {
    protected LSFGroupObjectReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected Function<LSFFormExtend, Collection<LSFGroupObjectDeclaration>> getElementsCollector() {
        return LSFFormExtend::getGroupObjectDecls;
    }

    @Nullable
    public List<LSFClassSet> resolveClasses() {
        LSFGroupObjectDeclaration decl = resolveDecl();
        if(decl == null)
            return null;
        return decl.resolveClasses();
    }

    @Override
    protected boolean isDeclarationType(PsiElement element) {
        return element instanceof LSFGroupObjectDeclaration;
    }
}
