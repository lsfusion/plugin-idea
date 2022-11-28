package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFStatementActionDeclaration;
import com.lsfusion.lang.psi.impl.LSFActionStatementImpl;
import com.lsfusion.lang.psi.stubs.StatementActionStubElement;
import com.lsfusion.lang.psi.stubs.impl.StatementActionStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

//тут LSFStatementActionDeclaration нужен, всё по аналогии с StatementPropStubElementType
public class StatementActionStubElementType extends ActionStubElementType<StatementActionStubElement, LSFStatementActionDeclaration> {

    public StatementActionStubElementType() {
        super("ACTION");
    }

    @Override
    public LSFStatementActionDeclaration createPsi(@NotNull StatementActionStubElement stub) {
        return new LSFActionStatementImpl(stub, this);
    }

    @Override
    public StatementActionStubElement createStub(@NotNull LSFStatementActionDeclaration psi, StubElement parentStub) {
        return new StatementActionStubImpl(parentStub, psi);
    }

    @Override
    public StatementActionStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new StatementActionStubImpl(dataStream, parentStub, this);
    }

}
