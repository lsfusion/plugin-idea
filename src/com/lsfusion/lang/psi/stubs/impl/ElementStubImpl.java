package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.*;
import com.lsfusion.lang.psi.LSFStubElement;
import com.lsfusion.lang.psi.LSFStubbedElement;

import java.io.IOException;

public abstract class ElementStubImpl<This extends LSFStubElement<This, Decl>, Decl extends LSFStubbedElement<Decl, This>>
        extends StubBase<Decl>
        implements LSFStubElement<This, Decl> {

    private boolean isCorrect;
    private boolean isInMetaDecl;

    public ElementStubImpl(StubElement parent, Decl psi) {
        super(parent, psi.getElementType());

        isCorrect = psi.isCorrect();
        isInMetaDecl = psi.isInMetaDecl();
    }

    public ElementStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<This, Decl> type) throws IOException {
        super(parentStub, type);

        isCorrect = dataStream.readBoolean();
        isInMetaDecl = dataStream.readBoolean();
   }

    protected ElementStubImpl(StubElement parent, IStubElementType elementType) {
        super(parent, elementType);

        this.isCorrect = true;
        this.isInMetaDecl = false;
    }

    @Override
    public void serialize(StubOutputStream dataStream) throws IOException {
        dataStream.writeBoolean(isCorrect);
        dataStream.writeBoolean(isInMetaDecl);
    }

    @Override
    public boolean isCorrect() {
        return isCorrect;
    }

    @Override
    public boolean isInMetaDecl() {
        return isInMetaDecl;
    }
}