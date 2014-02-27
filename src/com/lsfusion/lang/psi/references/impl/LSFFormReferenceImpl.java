package com.lsfusion.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.psi.declarations.LSFFormDeclaration;
import com.lsfusion.psi.references.LSFFormReference;
import com.lsfusion.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public abstract class LSFFormReferenceImpl extends LSFFullNameReferenceImpl<LSFFormDeclaration, LSFFormDeclaration> implements LSFFormReference {

    protected LSFFormReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected FullNameStubElementType getStubElementType() {
        return LSFStubElementTypes.FORM;
    }
}
