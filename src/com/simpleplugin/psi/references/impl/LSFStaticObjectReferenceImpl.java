package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.util.CollectionQuery;
import com.intellij.util.Query;
import com.simpleplugin.psi.LSFCustomClassUsage;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.declarations.LSFDeclaration;
import com.simpleplugin.psi.declarations.LSFStaticObjectDeclaration;
import com.simpleplugin.psi.extend.LSFClassExtend;
import com.simpleplugin.psi.references.LSFStaticObjectReference;
import com.simpleplugin.psi.stubs.extend.ExtendClassStubElement;
import com.simpleplugin.psi.stubs.extend.impl.ExtendClassStubImpl;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class LSFStaticObjectReferenceImpl extends LSFReferenceImpl<LSFStaticObjectDeclaration> implements LSFStaticObjectReference {
    public LSFStaticObjectReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }
    
    protected abstract LSFCustomClassUsage getCustomClassUsage();

    @Override
    protected void fillListVariants(Collection<String> variants) {
    }

    @Override
    public Query resolveNoCache() {
        List<LSFStaticObjectDeclaration> decls = new ArrayList<LSFStaticObjectDeclaration>();
        for (LSFClassExtend classExtend : LSFGlobalResolver.findExtendElements(getCustomClassUsage().resolveDecl(), LSFStubElementTypes.EXTENDCLASS, getProject(), getScope())) {
            for (LSFStaticObjectDeclaration staticDecl : classExtend.getStaticObjects()) {
                if (getSimpleName().getName() != null && staticDecl != null && getSimpleName().getName().equals(staticDecl.getName())) {
                    decls.add(staticDecl);
                }
            }
        }
        return new CollectionQuery<LSFStaticObjectDeclaration>(decls);
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
