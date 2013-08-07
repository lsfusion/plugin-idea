package com.simpleplugin.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.simpleplugin.psi.declarations.LSFMetaDeclaration;
import com.simpleplugin.psi.stubs.MetaStubElement;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import com.simpleplugin.psi.stubs.types.MetaStubElementType;

import java.io.IOException;

public class MetaStubImpl extends FullNameStubImpl<MetaStubElement, LSFMetaDeclaration> implements MetaStubElement {

    private int paramCount;

    public MetaStubImpl(StubElement parent, LSFMetaDeclaration psi) {
        super(parent, psi);

        paramCount = psi.getParamCount();
    }

    // виртуальный конструктор для гененрац
    public MetaStubImpl(String name, int paramCount) {
        super(null, LSFStubElementTypes.META, StringRef.fromString(name));
        this.paramCount = paramCount;
    }

    public void serialize(StubOutputStream dataStream) throws IOException {
        super.serialize(dataStream);

        dataStream.writeVarInt(paramCount);
    }

    public MetaStubImpl(StubInputStream dataStream, StubElement parentStub, MetaStubElementType type) throws IOException {
        super(dataStream, parentStub, type);

        paramCount = dataStream.readVarInt();
    }

    public int getParamCount() {
        return paramCount;
    }
}
