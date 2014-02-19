package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.psi.declarations.LSFGroupDeclaration;
import com.simpleplugin.psi.references.LSFGroupReference;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public abstract class LSFGroupReferenceImpl extends LSFFullNameReferenceImpl<LSFGroupDeclaration, LSFGroupDeclaration> implements LSFGroupReference {

    public LSFGroupReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected FullNameStubElementType<?, LSFGroupDeclaration> getStubElementType() {
        return LSFStubElementTypes.GROUP;
    }

}
