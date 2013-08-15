package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.psi.declarations.LSFGroupDeclaration;
import com.simpleplugin.psi.declarations.LSFWindowDeclaration;
import com.simpleplugin.psi.references.LSFWindowReference;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public abstract class LSFWindowReferenceImpl extends LSFFullNameReferenceImpl<LSFWindowDeclaration, LSFWindowDeclaration> implements LSFWindowReference {
    
    public LSFWindowReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected FullNameStubElementType<?, LSFWindowDeclaration> getType() {
        return LSFStubElementTypes.WINDOW;
    }

}
