package com.lsfusion.lang.psi.stubs.extend.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.stubs.extend.ExtendStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendStubElementType;
import com.lsfusion.lang.psi.stubs.impl.GlobalStubImpl;

import java.io.IOException;

public abstract class ExtendStubImpl<T extends LSFExtend<T, Stub>, Stub extends ExtendStubElement<T, Stub>> extends GlobalStubImpl<Stub, T> implements ExtendStubElement<T, Stub> {

    protected ExtendStubImpl(StubElement parent, T psi) {
        super(parent, psi);
    }

    protected ExtendStubImpl(StubInputStream dataStream, StubElement parentStub, ExtendStubElementType<T, Stub> type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
