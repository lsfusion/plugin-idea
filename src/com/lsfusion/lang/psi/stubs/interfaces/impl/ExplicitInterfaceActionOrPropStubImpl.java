package com.lsfusion.lang.psi.stubs.interfaces.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionOrPropStatement;
import com.lsfusion.lang.psi.stubs.impl.ElementStubImpl;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceActionOrPropStubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public abstract class ExplicitInterfaceActionOrPropStubImpl<This extends ExplicitInterfaceActionOrPropStubElement<This, Decl>, Decl extends LSFExplicitInterfaceActionOrPropStatement<Decl, This>> extends ElementStubImpl<This, Decl> implements ExplicitInterfaceActionOrPropStubElement<This, Decl> {

    private String name;
    private LSFExplicitClasses paramClasses;
    private byte propType;

    public ExplicitInterfaceActionOrPropStubImpl(StubElement parent, @NotNull final Decl psi) {
        super(parent, psi);

        this.name = psi.getName();
        this.paramClasses = psi.getExplicitParams();
        this.propType = psi.getPropType();
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

    public ExplicitInterfaceActionOrPropStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<This, Decl> type) throws IOException {
        super(dataStream, parentStub, type);

        StringRef nameRef = dataStream.readName();
        name = nameRef != null ? nameRef.getString() : null;
        paramClasses = dataStream.readBoolean() ? LSFExplicitClasses.deserialize(dataStream) : null;
        propType = dataStream.readByte();
    }

    @Override
    public void serialize(StubOutputStream dataStream) throws IOException {
        super.serialize(dataStream);

        dataStream.writeName(name);

        LSFExplicitClasses params = paramClasses;
        dataStream.writeBoolean(params != null);
        if (params != null) {
            params.serialize(dataStream);
        }

        dataStream.writeByte(propType);
    }
}
