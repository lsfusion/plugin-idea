package com.lsfusion.psi.stubs.extend.impl;

import com.intellij.psi.stubs.*;
import com.lsfusion.psi.extend.LSFExtend;
import com.lsfusion.psi.stubs.extend.ExtendStubElement;
import com.lsfusion.psi.stubs.extend.types.ExtendStubElementType;
import com.lsfusion.psi.stubs.impl.GlobalStubImpl;

import java.io.IOException;

public abstract class ExtendStubImpl<T extends LSFExtend<T, Stub>, Stub extends ExtendStubElement<T, Stub>> extends GlobalStubImpl<Stub, T> implements ExtendStubElement<T, Stub> {

    protected ExtendStubImpl(StubElement parent, T psi) {
        super(parent, psi);
    }

    protected ExtendStubImpl(StubInputStream dataStream, StubElement parentStub, ExtendStubElementType<T, Stub> type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
