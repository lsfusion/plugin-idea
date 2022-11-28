package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFBaseEventActionDeclaration;
import com.lsfusion.lang.psi.stubs.BaseEventActionStubElement;

import java.io.IOException;

//по аналогии с AggrParamPropStubImpl
public class BaseEventActionStubImpl extends ActionStubImpl<BaseEventActionStubElement, LSFBaseEventActionDeclaration> implements BaseEventActionStubElement {

    public BaseEventActionStubImpl(StubElement parent, LSFBaseEventActionDeclaration psi) {
        super(parent, psi);
    }

    public BaseEventActionStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<BaseEventActionStubElement, LSFBaseEventActionDeclaration> type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
