package com.lsfusion.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.psi.declarations.LSFWindowDeclaration;
import com.lsfusion.psi.references.LSFWindowReference;
import com.lsfusion.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public abstract class LSFWindowReferenceImpl extends LSFFullNameReferenceImpl<LSFWindowDeclaration, LSFWindowDeclaration> implements LSFWindowReference {
    
    public LSFWindowReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected FullNameStubElementType<?, LSFWindowDeclaration> getStubElementType() {
        return LSFStubElementTypes.WINDOW;
    }
}
