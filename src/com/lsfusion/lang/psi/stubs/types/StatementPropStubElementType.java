package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFStatementGlobalPropDeclaration;
import com.lsfusion.lang.psi.impl.LSFPropertyStatementImpl;
import com.lsfusion.lang.psi.stubs.StatementPropStubElement;
import com.lsfusion.lang.psi.stubs.impl.StatementPropStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class StatementPropStubElementType extends PropStubElementType<StatementPropStubElement, LSFStatementGlobalPropDeclaration> {

    public StatementPropStubElementType() {
        super("STATEMENT_PROP");
    }

    @Override
    public LSFStatementGlobalPropDeclaration createPsi(@NotNull StatementPropStubElement stub) {
        return new LSFPropertyStatementImpl(stub, this);
    }

    @NotNull
    @Override
    public StatementPropStubElement createStub(@NotNull LSFStatementGlobalPropDeclaration psi, StubElement parentStub) {
        return new StatementPropStubImpl(parentStub, psi);
    }

    @Override
    public StatementPropStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new StatementPropStubImpl(dataStream, parentStub, this);
    }

}
