package com.lsfusion.lang.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.LSFImplicitExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionStatement;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.declarations.impl.LSFExplicitInterfaceActionStatementImpl;
import com.lsfusion.lang.psi.impl.LSFExplicitInterfaceActStatementImpl;
import com.lsfusion.lang.psi.impl.LSFExplicitInterfacePropertyStatementImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceActionStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfacePropStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.impl.ExplicitInterfaceActionStubImpl;
import com.lsfusion.lang.psi.stubs.interfaces.impl.ExplicitInterfacePropStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

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

    protected ExplicitInterfaceActionStubElement deserialize(StubElement parentStub, String name, LSFExplicitClasses params, byte propType, @NotNull StubInputStream dataStream) throws IOException {
        return new ExplicitInterfaceActionStubImpl(parentStub, this, name, params, propType);
    }

    @Override
    protected StubIndexKey getIndexKey() {
        return LSFIndexKeys.EXPLICIT_INTERFACE_ACTION;
    }
}
