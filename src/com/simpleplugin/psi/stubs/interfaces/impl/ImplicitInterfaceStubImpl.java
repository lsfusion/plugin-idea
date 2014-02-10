package com.simpleplugin.psi.stubs.interfaces.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.simpleplugin.psi.declarations.LSFImplicitInterfacePropStatement;
import com.simpleplugin.psi.stubs.interfaces.ImplicitInterfaceStubElement;
import com.simpleplugin.psi.stubs.interfaces.types.ImplicitInterfaceStubElementType;

import java.io.IOException;
import java.util.List;

public class ImplicitInterfaceStubImpl extends StubBase<LSFImplicitInterfacePropStatement> implements ImplicitInterfaceStubElement {
    private List<String> paramProperties;

    public ImplicitInterfaceStubImpl(StubElement parent, IStubElementType elementType) {
        super(parent, elementType);
    }

    public ImplicitInterfaceStubImpl(StubElement parentStub, ImplicitInterfaceStubElementType type, List<String> params) throws IOException {
        this(parentStub, type);
        this.paramProperties = params;
    }

    @Override
    public List<String> getParamProperties() {
        return paramProperties;
    }

    public void setParamProperties(List<String> paramProperties) {
        this.paramProperties = paramProperties;
    }
}
