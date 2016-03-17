package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.LSFCustomClassUsage;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.LSFResolveResult;
import com.lsfusion.lang.psi.declarations.LSFStaticObjectDeclaration;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.references.LSFStaticObjectReference;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class LSFStaticObjectReferenceImpl extends LSFReferenceImpl<LSFStaticObjectDeclaration> implements LSFStaticObjectReference {
    public LSFStaticObjectReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected abstract LSFCustomClassUsage getCustomClassUsage();

    @Override
    public LSFResolveResult resolveNoCache() {
        List<LSFStaticObjectDeclaration> decls = new ArrayList<>();
        for (LSFClassExtend classExtend : LSFGlobalResolver.findExtendElements(getCustomClassUsage().resolveDecl(), LSFStubElementTypes.EXTENDCLASS, getLSFFile())) {
            for (LSFStaticObjectDeclaration staticDecl : classExtend.getStaticObjects()) {
                if (getSimpleName().getName() != null && staticDecl != null && getSimpleName().getName().equals(staticDecl.getName())) {
                    decls.add(staticDecl);
                }
            }
        }
        return new LSFResolveResult(decls, resolveDefaultErrorAnnotator(decls));
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
