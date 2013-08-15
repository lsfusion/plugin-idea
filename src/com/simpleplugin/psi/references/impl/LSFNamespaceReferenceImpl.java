package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.util.Query;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.declarations.LSFNamespaceDeclaration;
import com.simpleplugin.psi.references.LSFNamespaceReference;
import com.simpleplugin.psi.stubs.types.indexes.ExplicitNamespaceIndex;
import com.simpleplugin.psi.stubs.types.indexes.ModuleIndex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class LSFNamespaceReferenceImpl extends LSFGlobalReferenceImpl<LSFNamespaceDeclaration> implements LSFNamespaceReference {

    public LSFNamespaceReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Query<LSFNamespaceDeclaration> resolveNoCache() {
        return LSFGlobalResolver.findNamespaces(getNameRef(), getScope());
    }

    @Override
    protected Collection<StringStubIndexExtension> getGlobalIndices() {
        List<StringStubIndexExtension> result = new ArrayList<StringStubIndexExtension>();
        result.add(ModuleIndex.getInstance());
        result.add(ExplicitNamespaceIndex.getInstance());
        return result;
    }
}
