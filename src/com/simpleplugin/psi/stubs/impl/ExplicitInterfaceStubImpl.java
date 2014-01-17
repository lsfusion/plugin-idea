package com.simpleplugin.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.simpleplugin.psi.declarations.LSFExplicitInterfacePropStatement;
import com.simpleplugin.psi.stubs.ExplicitInterfaceStubElement;
import com.simpleplugin.psi.stubs.types.ExplicitInterfaceStubElementType;

import java.io.IOException;
import java.util.List;

public class ExplicitInterfaceStubImpl extends StubBase<LSFExplicitInterfacePropStatement> implements ExplicitInterfaceStubElement {
    public List<String> paramClasses;

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
}
