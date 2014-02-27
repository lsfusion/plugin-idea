package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;
import com.lsfusion.lang.psi.references.LSFTableReference;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
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
