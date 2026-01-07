package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFPropertyExtend;
import com.lsfusion.lang.psi.stubs.ExtendPropertyStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendStubElementType;
import com.lsfusion.lang.psi.stubs.extend.impl.ExtendStubImpl;

import java.io.IOException;

public class ExtendPropertyStubImpl extends ExtendStubImpl<LSFPropertyExtend, ExtendPropertyStubElement> implements ExtendPropertyStubElement {

    public ExtendPropertyStubImpl(StubElement parent, LSFPropertyExtend psi) {
        super(parent, psi);
    }

    public ExtendPropertyStubImpl(StubInputStream dataStream, StubElement parentStub, ExtendStubElementType<LSFPropertyExtend, ExtendPropertyStubElement> type) throws IOException {
        super(dataStream, parentStub, type);
    }
}