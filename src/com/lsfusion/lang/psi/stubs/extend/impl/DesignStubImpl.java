package com.lsfusion.lang.psi.stubs.extend.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.extend.LSFDesign;
import com.lsfusion.lang.psi.stubs.extend.DesignStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.DesignStubElementType;

import java.io.IOException;

public class DesignStubImpl extends ExtendStubImpl<LSFDesign, DesignStubElement> implements DesignStubElement {

    public DesignStubImpl(StubElement parent, LSFDesign psi) {
        super(parent, psi);
    }

    public DesignStubImpl(StubInputStream dataStream, StubElement parentStub, DesignStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}