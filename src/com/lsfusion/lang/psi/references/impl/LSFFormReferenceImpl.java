package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.references.LSFFormReference;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public abstract class LSFFormReferenceImpl extends LSFFullNameReferenceImpl<LSFFormDeclaration, LSFFormDeclaration> implements LSFFormReference {

    protected LSFFormReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected FullNameStubElementType getStubElementType() {
        return LSFStubElementTypes.FORM;
    }

    @Override
    public LSFId getFormUsageNameIdentifier() {
        return getCompoundID().getSimpleName();
    }
}
