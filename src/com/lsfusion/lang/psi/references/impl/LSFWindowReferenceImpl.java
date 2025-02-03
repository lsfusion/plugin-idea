package com.lsfusion.lang.psi.references.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.psi.declarations.LSFWindowDeclaration;
import com.lsfusion.lang.psi.references.LSFWindowReference;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.lang.psi.stubs.types.WindowStubElementType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class LSFWindowReferenceImpl extends LSFFullNameReferenceImpl<LSFWindowDeclaration, LSFWindowDeclaration> implements LSFWindowReference {
    
    public LSFWindowReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected WindowStubElementType getStubElementType() {
        return LSFStubElementTypes.WINDOW;
    }

    @Override
    protected List<LSFWindowDeclaration> getVirtDecls() {
        Collection<LSFWindowDeclaration> builtInWindows = LSFElementGenerator.getBuiltInWindows(getProject());
        return new ArrayList<>(builtInWindows);
    }

    @Override
    protected boolean isDeclarationType(PsiElement element) {
        return element instanceof LSFWindowDeclaration;
    }
}
