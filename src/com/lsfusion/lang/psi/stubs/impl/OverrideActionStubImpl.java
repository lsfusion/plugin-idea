package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFOverrideActionDeclaration;
import com.lsfusion.lang.psi.stubs.OverrideActionStubElement;
import com.lsfusion.lang.psi.stubs.types.OverrideActionStubElementType;

import java.io.IOException;

public class OverrideActionStubImpl extends FullNameStubImpl<OverrideActionStubElement, LSFOverrideActionDeclaration> implements OverrideActionStubElement {

    public OverrideActionStubImpl(StubElement parent, LSFOverrideActionDeclaration psi) {
        super(parent, psi);
    }

    public OverrideActionStubImpl(StubInputStream dataStream, StubElement parentStub, OverrideActionStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}