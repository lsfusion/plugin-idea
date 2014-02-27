package com.lsfusion.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.psi.declarations.LSFTableDeclaration;
import com.lsfusion.psi.references.LSFTableReference;
import com.lsfusion.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
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
