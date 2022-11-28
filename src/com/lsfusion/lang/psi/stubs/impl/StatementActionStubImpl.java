package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFStatementActionDeclaration;
import com.lsfusion.lang.psi.stubs.StatementActionStubElement;

import java.io.IOException;
//по аналогии с StatementPropStubImpl
public class StatementActionStubImpl extends ActionStubImpl<StatementActionStubElement, LSFStatementActionDeclaration> implements StatementActionStubElement {

    public StatementActionStubImpl(StubElement parent, LSFStatementActionDeclaration psi) {
        super(parent, psi);
    }

    public StatementActionStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<StatementActionStubElement, LSFStatementActionDeclaration> type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
