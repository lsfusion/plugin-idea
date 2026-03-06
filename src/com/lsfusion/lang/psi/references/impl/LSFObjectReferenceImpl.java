package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFObjectDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFObjectReference;
import com.lsfusion.lang.typeinfer.InferExResult;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

public abstract class LSFObjectReferenceImpl extends LSFFormElementReferenceImpl<LSFObjectDeclaration> implements LSFObjectReference {

    protected LSFObjectReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected boolean isDeclarationType(PsiElement element) {
        return element instanceof LSFObjectDeclaration;
    }

    @Nullable
    @Override
    public LSFClassSet resolveClass() {
        LSFObjectDeclaration decl = resolveDecl();
        if (decl == null)
            return null;
        return decl.resolveClass();
    }

    @Override
    protected Function<LSFFormExtend, Collection<LSFObjectDeclaration>> getElementsCollector() {
        return LSFFormExtend::getObjectDecls;
    }
}
