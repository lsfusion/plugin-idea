package com.lsfusion.lang.psi;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.lang.psi.stubs.GlobalStubElement;
import org.jetbrains.annotations.NotNull;

public abstract class LSFStubBasedPsiElement<This extends LSFGlobalElement<This, Stub>, Stub extends GlobalStubElement<Stub, This>>
        extends StubBasedPsiElementBase<Stub>
        implements LSFElement {
    
    protected LSFStubBasedPsiElement(@NotNull Stub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    protected LSFStubBasedPsiElement(@NotNull ASTNode node) {
        super(node);
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
