package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFOverridePropertyDeclaration;
import com.lsfusion.lang.psi.stubs.OverridePropertyStubElement;
import com.lsfusion.lang.psi.stubs.types.OverridePropertyStubElementType;

import java.io.IOException;

public class OverridePropertyStubImpl extends FullNameStubImpl<OverridePropertyStubElement, LSFOverridePropertyDeclaration> implements OverridePropertyStubElement {

    public OverridePropertyStubImpl(StubElement parent, LSFOverridePropertyDeclaration psi) {
        super(parent, psi);
    }

    public OverridePropertyStubImpl(StubInputStream dataStream, StubElement parentStub, OverridePropertyStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}