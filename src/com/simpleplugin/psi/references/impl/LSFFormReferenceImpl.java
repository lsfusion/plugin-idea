package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.psi.declarations.LSFFormDeclaration;
import com.simpleplugin.psi.references.LSFFormReference;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public abstract class LSFFormReferenceImpl extends LSFFullNameReferenceImpl<LSFFormDeclaration, LSFFormDeclaration> implements LSFFormReference {

    protected LSFFormReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.FORM;
    }
}
