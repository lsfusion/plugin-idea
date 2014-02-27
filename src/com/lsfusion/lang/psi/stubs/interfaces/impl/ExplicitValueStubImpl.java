package com.lsfusion.psi.stubs.interfaces.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.lsfusion.psi.declarations.LSFExplicitValuePropStatement;
import com.lsfusion.psi.stubs.interfaces.ExplicitValueStubElement;
import com.lsfusion.psi.stubs.interfaces.types.ExplicitValueStubElementType;

import java.io.IOException;
import java.util.List;

public class ExplicitValueStubImpl extends StubBase<LSFExplicitValuePropStatement> implements ExplicitValueStubElement {
    private List<String> valueClasses;

    public ExplicitValueStubImpl(StubElement parent, IStubElementType elementType) {
        super(parent, elementType);
    }

    public ExplicitValueStubImpl(StubElement parentStub, ExplicitValueStubElementType type, List<String> valueClasses) throws IOException {
        this(parentStub, type);
        this.valueClasses = valueClasses;
    }

    @Override
    public List<String> getValueClasses() {
        return valueClasses;
    }

    public void setValueClasses(List<String> valueClasses) {
        this.valueClasses = valueClasses;
    }
}
