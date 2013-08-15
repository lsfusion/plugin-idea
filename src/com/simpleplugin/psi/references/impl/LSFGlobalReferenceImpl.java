package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.simpleplugin.psi.declarations.LSFDeclaration;
import com.simpleplugin.psi.references.LSFGlobalReference;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class LSFGlobalReferenceImpl<T extends LSFDeclaration> extends LSFReferenceImpl<T> implements LSFGlobalReference<T> {

    protected LSFGlobalReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    public boolean isSoft() {
        return false;
    }
    
    protected abstract Collection<StringStubIndexExtension> getGlobalIndices();

    @Override
    protected void fillListVariants(Collection<String> variants) {
        for(StringStubIndexExtension index : getGlobalIndices())
            variants.addAll(index.getAllKeys(getProject()));
    }
}
