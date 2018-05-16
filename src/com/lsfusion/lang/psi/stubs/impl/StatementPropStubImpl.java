package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFStatementGlobalPropDeclaration;
import com.lsfusion.lang.psi.stubs.StatementPropStubElement;

import java.io.IOException;

public class StatementPropStubImpl extends PropStubImpl<StatementPropStubElement, LSFStatementGlobalPropDeclaration> implements StatementPropStubElement {

    public StatementPropStubImpl(StubElement parent, LSFStatementGlobalPropDeclaration psi) {
        super(parent, psi);
    }

    public StatementPropStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<StatementPropStubElement, LSFStatementGlobalPropDeclaration> type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
