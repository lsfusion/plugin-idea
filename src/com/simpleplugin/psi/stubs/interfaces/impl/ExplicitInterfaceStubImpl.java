package com.simpleplugin.psi.stubs.interfaces.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.simpleplugin.psi.declarations.LSFExplicitInterfacePropStatement;
import com.simpleplugin.psi.stubs.interfaces.ExplicitInterfaceStubElement;
import com.simpleplugin.psi.stubs.interfaces.types.ExplicitInterfaceStubElementType;

import java.io.IOException;
import java.util.List;

public class ExplicitInterfaceStubImpl extends StubBase<LSFExplicitInterfacePropStatement> implements ExplicitInterfaceStubElement {
    private List<String> paramClasses;

    public ExplicitInterfaceStubImpl(StubElement parent, IStubElementType elementType) {
        super(parent, elementType);
    }

    public ExplicitInterfaceStubImpl(StubElement parentStub, ExplicitInterfaceStubElementType type, List<String> params) throws IOException {
        this(parentStub, type);
        this.paramClasses = params;
    }

    @Override
    public List<String> getParamClasses() {
        return paramClasses;
    }

    public void setParamClasses(List<String> paramClasses) {
        this.paramClasses = paramClasses;
    }
}
