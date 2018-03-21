package com.lsfusion.lang.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.LSFImplicitExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.impl.LSFExplicitInterfacePropertyStatementImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfacePropStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.impl.ExplicitInterfacePropStubImpl;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
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

    @Override
    public void serialize(@NotNull ExplicitInterfacePropStubElement stub, @NotNull StubOutputStream dataStream) throws IOException {
        super.serialize(stub, dataStream);
        
        Set<String> values = stub.getParamExplicitValues();
        dataStream.writeBoolean(values != null);
        if (values != null) {
            LSFImplicitExplicitClasses.serializeSet(dataStream, values);
        }
    }

    protected ExplicitInterfacePropStubElement deserialize(StubElement parentStub, String name, LSFExplicitClasses params, byte propType, @NotNull StubInputStream dataStream) throws IOException {
        return new ExplicitInterfacePropStubImpl(parentStub, this, name, params, propType, dataStream.readBoolean() ? LSFImplicitExplicitClasses.deserializeSet(dataStream) : null);
    }

    @Override
    protected StubIndexKey getIndexKey() {
        return LSFIndexKeys.EXPLICIT_INTERFACE_PROP;
    }
}
