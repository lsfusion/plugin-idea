package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.LSFGlobalElement;
import com.lsfusion.lang.psi.stubs.GlobalStubElement;

import java.io.IOException;

public abstract class GlobalStubImpl<This extends GlobalStubElement<This, Decl>, Decl extends LSFGlobalElement<Decl, This>>
        extends StubBase<Decl>
        implements GlobalStubElement<This, Decl> {

    protected StringRef name;
    protected boolean isCorrect;

    protected GlobalStubImpl(StubElement parent, Decl psi) {
        super(parent, psi.getElementType());

        name = StringRef.fromString(psi.getGlobalName());
        isCorrect = psi.isCorrect();
    }

    public GlobalStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<This, Decl> type) throws IOException {
        super(parentStub, type);

        name = dataStream.readName();
        isCorrect = dataStream.readBoolean();
    }

    protected GlobalStubImpl(StubElement parent, IStubElementType elementType, StringRef name) {
        super(parent, elementType);
        this.name = name;
        this.isCorrect = true;
    }

    public void serialize(StubOutputStream dataStream) throws IOException {
        dataStream.writeName(StringRef.toString(name));

        dataStream.writeBoolean(isCorrect);
    }

    @Override
    public String getGlobalName() {
        return StringRef.toString(name);
    }

    @Override
    public boolean isCorrect() {
        return isCorrect;
    }
}
