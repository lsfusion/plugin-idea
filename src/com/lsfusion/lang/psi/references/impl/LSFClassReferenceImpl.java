package com.lsfusion.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.psi.declarations.LSFClassDeclaration;
import com.lsfusion.psi.references.LSFClassReference;
import com.lsfusion.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
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
