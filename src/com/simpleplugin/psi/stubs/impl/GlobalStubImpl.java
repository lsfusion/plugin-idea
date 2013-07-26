package com.simpleplugin.psi.stubs.impl;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.simpleplugin.psi.declarations.LSFGlobalDeclaration;
import com.simpleplugin.psi.stubs.GlobalStubElement;

import java.io.IOException;

public abstract class GlobalStubImpl<This extends GlobalStubElement<This, Decl>, Decl extends LSFGlobalDeclaration<Decl, This>> extends StubBase<Decl> implements GlobalStubElement<This, Decl> {

    protected StringRef name;

    protected GlobalStubImpl(StubElement parent, Decl psi) {
        super(parent, psi.getElementType());

        name = StringRef.fromString(psi.getGlobalName());
    }

    public GlobalStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<This, Decl> type) throws IOException {
        super(parentStub, type);

        name = dataStream.readName();
    }

    public void serialize(StubOutputStream dataStream) throws IOException {
        dataStream.writeName(StringRef.toString(name));
    }

    @Override
    public String getGlobalName() {
        return StringRef.toString(name);
    }
}
