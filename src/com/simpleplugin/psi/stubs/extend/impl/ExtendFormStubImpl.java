package com.simpleplugin.psi.stubs.extend.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.simpleplugin.psi.extend.LSFFormExtend;
import com.simpleplugin.psi.stubs.extend.ExtendFormStubElement;
import com.simpleplugin.psi.stubs.extend.types.ExtendFormStubElementType;
import com.simpleplugin.psi.stubs.extend.types.ExtendStubElementType;

import java.io.IOException;

public class ExtendFormStubImpl extends ExtendStubImpl<LSFFormExtend, ExtendFormStubElement> implements ExtendFormStubElement {

    public ExtendFormStubImpl(StubElement parent, LSFFormExtend psi) {
        super(parent, psi);
    }

    public ExtendFormStubImpl(StubInputStream dataStream, StubElement parentStub, ExtendFormStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
