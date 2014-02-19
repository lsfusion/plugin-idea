package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.LSFDeclarationResolveResult;
import com.simpleplugin.psi.LSFCustomClassUsage;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.declarations.LSFStaticObjectDeclaration;
import com.simpleplugin.psi.extend.LSFClassExtend;
import com.simpleplugin.psi.references.LSFStaticObjectReference;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class LSFStaticObjectReferenceImpl extends LSFReferenceImpl<LSFStaticObjectDeclaration> implements LSFStaticObjectReference {
    public LSFStaticObjectReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }
    
    protected abstract LSFCustomClassUsage getCustomClassUsage();

    @Override
    public LSFDeclarationResolveResult resolveNoCache() {
        List<LSFStaticObjectDeclaration> decls = new ArrayList<LSFStaticObjectDeclaration>();
        for (LSFClassExtend classExtend : LSFGlobalResolver.findExtendElements(getCustomClassUsage().resolveDecl(), LSFStubElementTypes.EXTENDCLASS, getProject(), getScope())) {
            for (LSFStaticObjectDeclaration staticDecl : classExtend.getStaticObjects()) {
                if (getSimpleName().getName() != null && staticDecl != null && getSimpleName().getName().equals(staticDecl.getName())) {
                    decls.add(staticDecl);
                }
            }
        }
        return new LSFDeclarationResolveResult(decls, resolveDefaultErrorAnnotator(decls));
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
