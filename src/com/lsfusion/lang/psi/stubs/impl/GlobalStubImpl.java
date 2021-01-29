package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.LSFGlobalElement;
import com.lsfusion.lang.psi.stubs.GlobalStubElement;

import java.io.IOException;

public abstract class GlobalStubImpl<This extends GlobalStubElement<This, Decl>, Decl extends LSFGlobalElement<Decl, This>>
        extends ElementStubImpl<This, Decl>
        implements GlobalStubElement<This, Decl> {

    protected StringRef name;

    protected GlobalStubImpl(StubElement parent, Decl psi) {
        super(parent, psi);

        name = StringRef.fromString(psi.getGlobalName());
    }

    public GlobalStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<This, Decl> type) throws IOException {
        super(dataStream, parentStub, type);

        name = dataStream.readName();
    }

    protected GlobalStubImpl(StubElement parent, IStubElementType elementType, StringRef name) {
        super(parent, elementType);
        this.name = name;
    }

    public void serialize(StubOutputStream dataStream) throws IOException {
        super.serialize(dataStream);

        dataStream.writeName(StringRef.toString(name));
    }

    @Override
    public String getGlobalName() {
        return StringRef.toString(name);
    }
}
