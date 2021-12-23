package com.lsfusion.lang.psi.stubs.interfaces.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.declarations.LSFImplicitInterfacePropStatement;
import com.lsfusion.lang.psi.stubs.impl.ElementStubImpl;
import com.lsfusion.lang.psi.stubs.interfaces.ImplicitInterfaceStubElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImplicitInterfaceStubImpl extends ElementStubImpl<ImplicitInterfaceStubElement, LSFImplicitInterfacePropStatement> implements ImplicitInterfaceStubElement {
    private List<String> paramProperties;

    public ImplicitInterfaceStubImpl(StubElement parent, LSFImplicitInterfacePropStatement psi) {
        super(parent, psi);
    }

    public ImplicitInterfaceStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<ImplicitInterfaceStubElement, LSFImplicitInterfacePropStatement> type) throws IOException {
        super(dataStream, parentStub, type);

        int paramsCount = dataStream.readInt();
        List<String> params = new ArrayList<>();
        if (paramsCount != 0) {
            for (int i = 0; i < paramsCount; i++) {
                StringRef name = dataStream.readName();
                params.add(name != null ? name.getString() : null);
            }
        }
        this.paramProperties = params;
    }

    @Override
    public void serialize(StubOutputStream dataStream) throws IOException {
        super.serialize(dataStream);

        List<String> params = paramProperties;
        if (params != null) {
            dataStream.writeInt(params.size());
            for (String param : params) {
                dataStream.writeName(param);
            }
        } else {
            dataStream.writeInt(0);
        }
    }

    @Override
    public List<String> getParamProperties() {
        return paramProperties;
    }

    public void setParamProperties(List<String> paramProperties) {
        this.paramProperties = paramProperties;
    }
}
