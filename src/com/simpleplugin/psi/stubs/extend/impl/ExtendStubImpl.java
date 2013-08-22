package com.simpleplugin.psi.stubs.extend.impl;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.simpleplugin.psi.extend.LSFExtend;
import com.simpleplugin.psi.stubs.extend.ExtendStubElement;
import com.simpleplugin.psi.stubs.extend.types.ExtendStubElementType;
import com.simpleplugin.psi.stubs.impl.GlobalStubImpl;

import java.io.IOException;

public abstract class ExtendStubImpl<T extends LSFExtend<T, Stub>, Stub extends ExtendStubElement<T, Stub>> extends GlobalStubImpl<Stub, T> implements ExtendStubElement<T, Stub> {

    protected ExtendStubImpl(StubElement parent, T psi) {
        super(parent, psi);
    }

    protected ExtendStubImpl(StubInputStream dataStream, StubElement parentStub, ExtendStubElementType<T, Stub> type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
