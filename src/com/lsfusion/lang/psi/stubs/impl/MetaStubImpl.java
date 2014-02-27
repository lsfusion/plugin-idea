package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;
import com.lsfusion.lang.psi.stubs.MetaStubElement;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.lang.psi.stubs.types.MetaStubElementType;

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
