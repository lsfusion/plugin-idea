package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFActionExtend;
import com.lsfusion.lang.psi.stubs.ExtendActionStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendStubElementType;
import com.lsfusion.lang.psi.stubs.extend.impl.ExtendStubImpl;

import java.io.IOException;

public class ExtendActionStubImpl extends ExtendStubImpl<LSFActionExtend, ExtendActionStubElement> implements ExtendActionStubElement {

    public ExtendActionStubImpl(StubElement parent, LSFActionExtend psi) {
        super(parent, psi);
    }

    public ExtendActionStubImpl(StubInputStream dataStream, StubElement parentStub, ExtendStubElementType<LSFActionExtend, ExtendActionStubElement> type) throws IOException {
        super(dataStream, parentStub, type);
    }
}