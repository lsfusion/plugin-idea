package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.simpleplugin.LSFDeclarationResolveResult;
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
    public LSFDeclarationResolveResult resolveNoCache() {
        Collection<LSFModuleDeclaration> decls = LSFGlobalResolver.findModules(getNameRef(), getScope()).findAll();
        return new LSFDeclarationResolveResult(decls, resolveDefaultErrorAnnotator(decls));
    }

    @Override
    protected Collection<StringStubIndexExtension> getGlobalIndices() {
        return Collections.<StringStubIndexExtension>singleton(ModuleIndex.getInstance());
    }
}
