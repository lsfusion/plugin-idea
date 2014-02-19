package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.references.LSFClassReference;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public abstract class LSFClassReferenceImpl extends LSFFullNameReferenceImpl<LSFClassDeclaration, LSFClassDeclaration> implements LSFClassReference {

    public LSFClassReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected FullNameStubElementType<?, LSFClassDeclaration> getStubElementType() {
        return LSFStubElementTypes.CLASS;
    }
}
