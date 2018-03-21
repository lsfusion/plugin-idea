package com.lsfusion.lang.psi.stubs.interfaces.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfacePropStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ExplicitInterfacePropStubImpl extends ExplicitInterfaceActionOrPropStubImpl<LSFExplicitInterfacePropStatement> implements ExplicitInterfacePropStubElement {

    private Set<String> valueClasses;

    public ExplicitInterfacePropStubImpl(StubElement parent, @NotNull LSFExplicitInterfacePropStatement psi) {
        super(parent, psi);
        valueClasses = psi.getExplicitValues();
    }

    public ExplicitInterfacePropStubImpl(StubElement parentStub, IStubElementType type, String name, LSFExplicitClasses params, byte propType, Set<String> values) {
        super(parentStub, type, name, params, propType);
        valueClasses = values;
    }

    @Override
    @Nullable
    public Set<String> getParamExplicitValues() {
        return valueClasses;
    }
}
