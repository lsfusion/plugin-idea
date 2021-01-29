package com.lsfusion.lang.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.*;
import com.lsfusion.lang.psi.LSFImplicitExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.impl.LSFExplicitInterfacePropertyStatementImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfacePropStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.impl.ExplicitInterfacePropStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;


public class ExplicitInterfacePropStubElementType extends ExplicitInterfaceActionOrPropStubElementType<ExplicitInterfacePropStubElement, LSFExplicitInterfacePropStatement> {
    public ExplicitInterfacePropStubElementType() {
        super("EXPLICIT_INTERFACE_PROPERTY_STATEMENT");
    }

    @Override
    public LSFExplicitInterfacePropStatement createPsi(@NotNull ExplicitInterfacePropStubElement stub) {
        return new LSFExplicitInterfacePropertyStatementImpl(stub, this);
    }

    @Override
    public ExplicitInterfacePropStubElement createStub(@NotNull final LSFExplicitInterfacePropStatement psi, StubElement parentStub) {
        return new ExplicitInterfacePropStubImpl(parentStub, psi);
    }

    @NotNull
    public ExplicitInterfacePropStubElement deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ExplicitInterfacePropStubImpl(dataStream, parentStub, this);
    }

    @Override
    protected StubIndexKey getIndexKey() {
        return LSFIndexKeys.EXPLICIT_INTERFACE_PROP;
    }
}
