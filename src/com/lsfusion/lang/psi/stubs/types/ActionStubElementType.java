package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFActionDeclaration;
import com.lsfusion.lang.psi.impl.LSFActionStatementImpl;
import com.lsfusion.lang.psi.stubs.ActionStubElement;
import com.lsfusion.lang.psi.stubs.impl.ActionStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ActionStubElementType extends ActStubElementType<ActionStubElement, LSFActionDeclaration> {

    public ActionStubElementType() {
        super("ACTION");
    }

    @Override
    public LSFActionDeclaration createPsi(@NotNull ActionStubElement stub) {
        return new LSFActionStatementImpl(stub, this);
    }

    @Override
    public ActionStubElement createStub(@NotNull LSFActionDeclaration psi, StubElement parentStub) {
        return new ActionStubImpl(parentStub, psi);
    }

    @Override
    public ActionStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ActionStubImpl(dataStream, parentStub, this);
    }

}
