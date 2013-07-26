package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.psi.declarations.LSFPropDeclaration;
import com.simpleplugin.psi.references.LSFPropReference;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class LSFPropReferenceImpl extends LSFFullNameReferenceImpl<LSFPropDeclaration> implements LSFPropReference {

    public LSFPropReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected FullNameStubElementType<?, LSFPropDeclaration> getType() {
        return LSFStubElementTypes.PROP;
    }
}
