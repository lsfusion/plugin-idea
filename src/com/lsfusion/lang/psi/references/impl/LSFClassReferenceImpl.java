package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.references.LSFClassReference;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
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
