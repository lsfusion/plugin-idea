package com.lsfusion.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.psi.LSFResolveResult;
import com.lsfusion.psi.LSFCustomClassUsage;
import com.lsfusion.psi.LSFGlobalResolver;
import com.lsfusion.psi.declarations.LSFStaticObjectDeclaration;
import com.lsfusion.psi.extend.LSFClassExtend;
import com.lsfusion.psi.references.LSFStaticObjectReference;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
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
        List<LSFStaticObjectDeclaration> decls = new ArrayList<LSFStaticObjectDeclaration>();
        for (LSFClassExtend classExtend : LSFGlobalResolver.findExtendElements(getCustomClassUsage().resolveDecl(), LSFStubElementTypes.EXTENDCLASS, getProject(), getScope())) {
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
