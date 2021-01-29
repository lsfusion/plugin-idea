package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.stubs.GlobalStubElement;

import java.io.IOException;

public abstract class ElementStubImpl<This extends LSFStubElement<This, Decl>, Decl extends LSFStubbedElement<Decl, This>>
        extends StubBase<Decl>
        implements LSFStubElement<This, Decl> {

    private boolean isCorrect;

    public ElementStubImpl(StubElement parent, Decl psi) {
        super(parent, psi.getElementType());

        isCorrect = psi.isCorrect();
    }

    public ElementStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<This, Decl> type) throws IOException {
        super(parentStub, type);

        isCorrect = dataStream.readBoolean();
   }

    protected ElementStubImpl(StubElement parent, IStubElementType elementType) {
        super(parent, elementType);

        this.isCorrect = true;
    }

    @Override
    public void serialize(StubOutputStream dataStream) throws IOException {
        dataStream.writeBoolean(isCorrect);
    }

    @Override
    public boolean isCorrect() {
        return isCorrect;
    }
}