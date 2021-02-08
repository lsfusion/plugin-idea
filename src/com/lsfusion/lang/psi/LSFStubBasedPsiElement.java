package com.lsfusion.lang.psi;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.psi.stubs.GlobalStubElement;
import org.jetbrains.annotations.NotNull;

public abstract class LSFStubBasedPsiElement<This extends LSFStubbedElement<This, Stub>, Stub extends LSFStubElement<Stub, This>>
        extends StubBasedPsiElementBase<Stub>
        implements LSFStubbedElement<This, Stub> {
    
    protected LSFStubBasedPsiElement(@NotNull Stub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    protected LSFStubBasedPsiElement(@NotNull ASTNode node) {
        super(node);
    }

    public LSFStubBasedPsiElement(Stub stub, IElementType nodeType, ASTNode node) {
        super(stub, nodeType, node);
    }

    @Override
    public boolean isCorrect() {
        Stub stub = getStub();
        if(stub!=null)
            return stub.isCorrect();

        return LSFElementImpl.isCorrect(this);
    }

    @Override
    public boolean isInMetaDecl() {
        Stub stub = getStub();
        if(stub!=null)
            return stub.isInMetaDecl();

        return LSFReferenceAnnotator.isInMetaDecl(this);
    }

    @Override
    public LSFFile getLSFFile() {
        return (LSFFile) getContainingFile();
    }
}
