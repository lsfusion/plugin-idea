package com.simpleplugin.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.simpleplugin.psi.declarations.LSFFullNameDeclaration;
import com.simpleplugin.psi.references.LSFNavigatorElementReference;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class LSFNavigatorElementReferenceImpl extends LSFFullNameReferenceImpl<LSFFullNameDeclaration, LSFFullNameDeclaration> implements LSFNavigatorElementReference {

    protected LSFNavigatorElementReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    private final static List<FullNameStubElementType> types = Arrays.asList(
            new FullNameStubElementType[]{LSFStubElementTypes.NAVIGATORELEMENT, LSFStubElementTypes.FORM}); 
    
    @Override
    protected Collection<FullNameStubElementType> getStubElementTypes() {
        return types;
    }
}
