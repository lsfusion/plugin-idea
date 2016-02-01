package com.lsfusion.lang.psi.stubs.interfaces.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceStubElement;
import com.sun.istack.internal.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ExplicitInterfaceStubImpl extends StubBase<LSFExplicitInterfacePropStatement> implements ExplicitInterfaceStubElement {
    private String name;
    private LSFExplicitClasses paramClasses;
    private Set<String> valueClasses;
    private byte propType;

    public ExplicitInterfaceStubImpl(StubElement parent, @NotNull final LSFExplicitInterfacePropStatement psi) {
        this(parent, psi.getElementType(), psi.getName(), psi.getExplicitParams(), psi.getExplicitValues(), psi.getPropType());
    }

    public ExplicitInterfaceStubImpl(StubElement parentStub, IStubElementType type, String name, LSFExplicitClasses params, Set<String> values, byte propType) {
        super(parentStub, type);
        this.name = name;
        this.paramClasses = params;
        this.valueClasses = values;
        this.propType = propType;
    }

    @NotNull
    @Override
    public String getDeclName() {
        return name;
    }

    @Override
    @Nullable
    public LSFExplicitClasses getParamExplicitClasses() {
        return paramClasses;
    }
    @Override
    @Nullable
    public Set<String> getParamExplicitValues() {
        return valueClasses;
    }

    @Override
    public byte getPropType() {
        return propType;
    }
}
