package com.lsfusion.lang.psi.stubs.interfaces.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionStatement;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceActionStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfacePropStubElement;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ExplicitInterfaceActionStubImpl extends ExplicitInterfaceActionOrPropStubImpl<LSFExplicitInterfaceActionStatement> implements ExplicitInterfaceActionStubElement {

    public ExplicitInterfaceActionStubImpl(StubElement parent, @NotNull LSFExplicitInterfaceActionStatement psi) {
        super(parent, psi);
    }

    public ExplicitInterfaceActionStubImpl(StubElement parentStub, IStubElementType type, String name, LSFExplicitClasses params, byte propType) {
        super(parentStub, type, name, params, propType);
    }
}
