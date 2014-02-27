package com.lsfusion.psi.stubs.interfaces.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.lsfusion.psi.declarations.LSFImplicitValuePropStatement;
import com.lsfusion.psi.stubs.interfaces.ImplicitValueStubElement;
import com.lsfusion.psi.stubs.interfaces.types.ImplicitValueStubElementType;

import java.io.IOException;
import java.util.List;

public class ImplicitValueStubImpl extends StubBase<LSFImplicitValuePropStatement> implements ImplicitValueStubElement {
    private List<String> valueProperties;

    public ImplicitValueStubImpl(StubElement parent, IStubElementType elementType) {
        super(parent, elementType);
    }

    public ImplicitValueStubImpl(StubElement parentStub, ImplicitValueStubElementType type, List<String> valueProperties) throws IOException {
        this(parentStub, type);
        this.valueProperties = valueProperties;
    }

    @Override
    public List<String> getValueProperties() {
        return valueProperties;
    }

    public void setValueProperties(List<String> valueProperties) {
        this.valueProperties = valueProperties;
    }
}
