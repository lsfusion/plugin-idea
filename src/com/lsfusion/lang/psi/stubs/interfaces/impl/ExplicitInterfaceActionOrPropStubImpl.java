package com.lsfusion.lang.psi.stubs.interfaces.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionOrPropStatement;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceActionOrPropStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfacePropStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class ExplicitInterfaceActionOrPropStubImpl<T extends LSFExplicitInterfaceActionOrPropStatement> extends StubBase<T> implements ExplicitInterfaceActionOrPropStubElement<T> {

    private String name;
    private LSFExplicitClasses paramClasses;
    private byte propType;

    public ExplicitInterfaceActionOrPropStubImpl(StubElement parent, @NotNull final T psi) {
        this(parent, psi.getElementType(), psi.getName(), psi.getExplicitParams(), psi.getPropType());
    }

    public ExplicitInterfaceActionOrPropStubImpl(StubElement parentStub, IStubElementType type, String name, LSFExplicitClasses params, byte propType) {
        super(parentStub, type);
        this.name = name;
        this.paramClasses = params;
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
    public byte getPropType() {
        return propType;
    }
}
