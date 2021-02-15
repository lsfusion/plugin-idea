package com.lsfusion.lang.psi.stubs.interfaces.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.lsfusion.lang.psi.LSFImplicitExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfacePropStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Set;

public class ExplicitInterfacePropStubImpl extends ExplicitInterfaceActionOrPropStubImpl<ExplicitInterfacePropStubElement, LSFExplicitInterfacePropStatement> implements ExplicitInterfacePropStubElement {

    private Set<String> valueClasses;

    public ExplicitInterfacePropStubImpl(StubElement parent, @NotNull LSFExplicitInterfacePropStatement psi) {
        super(parent, psi);
        valueClasses = psi.getExplicitValues();
    }

    @Override
    @Nullable
    public Set<String> getParamExplicitValues() {
        return valueClasses;
    }

    public ExplicitInterfacePropStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<ExplicitInterfacePropStubElement, LSFExplicitInterfacePropStatement> type) throws IOException {
        super(dataStream, parentStub, type);

        valueClasses = dataStream.readBoolean() ? LSFImplicitExplicitClasses.deserializeSet(dataStream) : null;
    }

    @Override
    public void serialize(StubOutputStream dataStream) throws IOException {
        super.serialize(dataStream);

        Set<String> values = valueClasses;
        dataStream.writeBoolean(values != null);
        if (values != null) {
            LSFImplicitExplicitClasses.serializeSet(dataStream, values);
        }
    }
}
