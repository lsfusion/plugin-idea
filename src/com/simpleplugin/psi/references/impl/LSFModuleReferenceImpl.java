package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.util.Query;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.declarations.LSFModuleDeclaration;
import com.simpleplugin.psi.references.LSFModuleReference;
import com.simpleplugin.psi.stubs.types.indexes.ModuleIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public abstract class LSFModuleReferenceImpl extends LSFGlobalReferenceImpl<LSFModuleDeclaration> implements LSFModuleReference {

    protected LSFModuleReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Query<LSFModuleDeclaration> resolveNoCache() {
        return LSFGlobalResolver.findModules(getNameRef(), getScope());
    }

    @Override
    protected Collection<StringStubIndexExtension> getGlobalIndices() {
        return Collections.<StringStubIndexExtension>singleton(ModuleIndex.getInstance());
    }
}
