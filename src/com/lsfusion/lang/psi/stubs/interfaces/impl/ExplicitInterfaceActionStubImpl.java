package com.lsfusion.lang.psi.stubs.interfaces.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionStatement;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceActionStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfacePropStubElement;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

public class ExplicitInterfaceActionStubImpl extends ExplicitInterfaceActionOrPropStubImpl<ExplicitInterfaceActionStubElement, LSFExplicitInterfaceActionStatement> implements ExplicitInterfaceActionStubElement {

    public ExplicitInterfaceActionStubImpl(StubElement parent, @NotNull LSFExplicitInterfaceActionStatement psi) {
        super(parent, psi);
    }

    public ExplicitInterfaceActionStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<ExplicitInterfaceActionStubElement, LSFExplicitInterfaceActionStatement> type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
