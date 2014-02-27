package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;
import com.lsfusion.lang.psi.references.LSFNavigatorElementReference;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
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
