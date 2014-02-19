package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.psi.declarations.LSFGroupDeclaration;
import com.simpleplugin.psi.declarations.LSFTableDeclaration;
import com.simpleplugin.psi.references.LSFTableReference;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public abstract class LSFTableReferenceImpl extends LSFFullNameReferenceImpl<LSFTableDeclaration, LSFTableDeclaration> implements LSFTableReference {
    
    protected LSFTableReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected FullNameStubElementType<?, LSFTableDeclaration> getStubElementType() {
        return LSFStubElementTypes.TABLE;
    }

}
