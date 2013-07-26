package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.util.Query;
import com.simpleplugin.psi.LSFFile;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.references.LSFClassReference;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class LSFClassReferenceImpl extends LSFFullNameReferenceImpl<LSFClassDeclaration> implements LSFClassReference {

    public LSFClassReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected FullNameStubElementType<?, LSFClassDeclaration> getType() {
        return LSFStubElementTypes.CLASS;
    }
}
