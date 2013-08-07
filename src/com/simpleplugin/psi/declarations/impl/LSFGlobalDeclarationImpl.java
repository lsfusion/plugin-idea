package com.simpleplugin.psi.declarations.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.simpleplugin.psi.LSFElementImpl;
import com.simpleplugin.psi.LSFFile;
import com.simpleplugin.psi.declarations.LSFGlobalDeclaration;
import com.simpleplugin.psi.stubs.GlobalStubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

// множественное наследова
public abstract class LSFGlobalDeclarationImpl<This extends LSFGlobalDeclaration<This, Stub>, Stub extends GlobalStubElement<Stub, This>> extends StubBasedPsiElementBase<Stub> implements LSFGlobalDeclaration<This, Stub> {

    protected LSFGlobalDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFGlobalDeclarationImpl(@NotNull Stub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public String getGlobalName() {
        Stub stub = getStub();
        if(stub!=null)
            return stub.getGlobalName();

        return getName();
    }

    @Override
    public String getName() {
        return LSFDeclarationImpl.getName(this);
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return LSFDeclarationImpl.setName(this, name);
    }

    @Override
    public boolean isCorrect() { // множественное наследование по сути
        Stub stub = getStub();
        if(stub!=null)
            return stub.isCorrect();
        
        return LSFElementImpl.isCorrect(this);
    }

    @Override
    public LSFFile getLSFFile() {
        return (LSFFile) getContainingFile();
    }

    @Override
    public GlobalSearchScope getScope() {
        return LSFElementImpl.getScope(this);
    }
}
