package com.simpleplugin.psi.stubs.extend.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.simpleplugin.psi.extend.LSFClassExtend;
import com.simpleplugin.psi.stubs.extend.ExtendClassStubElement;
import com.simpleplugin.psi.stubs.extend.ExtendStubElement;
import com.simpleplugin.psi.stubs.extend.types.ExtendClassStubElementType;
import com.simpleplugin.psi.stubs.extend.types.ExtendStubElementType;

import java.io.IOException;

public class ExtendClassStubImpl extends ExtendStubImpl<LSFClassExtend, ExtendClassStubElement> implements ExtendClassStubElement {

    public ExtendClassStubImpl(StubElement parent, LSFClassExtend psi) {
        super(parent, psi);
    }

    public ExtendClassStubImpl(StubInputStream dataStream, StubElement parentStub, ExtendClassStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
