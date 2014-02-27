package com.lsfusion.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.psi.declarations.LSFGroupDeclaration;
import com.lsfusion.psi.references.LSFGroupReference;
import com.lsfusion.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
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
