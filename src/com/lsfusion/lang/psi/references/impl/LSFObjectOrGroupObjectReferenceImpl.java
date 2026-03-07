package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFObjectOrGroupObjectDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFObjectOrGroupObjectReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class LSFObjectOrGroupObjectReferenceImpl extends LSFFormElementReferenceImpl<LSFObjectOrGroupObjectDeclaration> implements LSFObjectOrGroupObjectReference {

    protected LSFObjectOrGroupObjectReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected Function<LSFFormExtend, Collection<LSFObjectOrGroupObjectDeclaration>> getElementsCollector() {
        return LSFFormExtend::getObjectOrGroupObjectDecls;
    }

    @Override
    protected boolean isDeclarationType(PsiElement element) {
        return element instanceof LSFObjectOrGroupObjectDeclaration;
    }
}
