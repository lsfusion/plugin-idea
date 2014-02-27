package com.lsfusion.lang.psi.stubs.extend.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.stubs.extend.ExtendFormStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendFormStubElementType;

import java.io.IOException;

public class ExtendFormStubImpl extends ExtendStubImpl<LSFFormExtend, ExtendFormStubElement> implements ExtendFormStubElement {

    public ExtendFormStubImpl(StubElement parent, LSFFormExtend psi) {
        super(parent, psi);
    }

    public ExtendFormStubImpl(StubInputStream dataStream, StubElement parentStub, ExtendFormStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
