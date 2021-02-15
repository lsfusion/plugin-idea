package com.lsfusion.lang.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionStatement;
import com.lsfusion.lang.psi.impl.LSFExplicitInterfaceActStatementImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceActionStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.impl.ExplicitInterfaceActionStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExplicitInterfaceActionStubElementType extends ExplicitInterfaceActionOrPropStubElementType<ExplicitInterfaceActionStubElement, LSFExplicitInterfaceActionStatement> {
    public ExplicitInterfaceActionStubElementType() {
        super("EXPLICIT_INTERFACE_ACT_STATEMENT");
    }

    @Override
    public LSFExplicitInterfaceActionStatement createPsi(@NotNull ExplicitInterfaceActionStubElement stub) {
        return new LSFExplicitInterfaceActStatementImpl(stub, this);
    }

    @Override
    public ExplicitInterfaceActionStubElement createStub(@NotNull final LSFExplicitInterfaceActionStatement psi, StubElement parentStub) {
        return new ExplicitInterfaceActionStubImpl(parentStub, psi);
    }

    @NotNull
    public ExplicitInterfaceActionStubElement deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ExplicitInterfaceActionStubImpl(dataStream, parentStub, this);
    }

    @Override
    protected StubIndexKey getIndexKey() {
        return LSFIndexKeys.EXPLICIT_INTERFACE_ACTION;
    }
}
