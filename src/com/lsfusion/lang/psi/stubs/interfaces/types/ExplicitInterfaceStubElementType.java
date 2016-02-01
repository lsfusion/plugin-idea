package com.lsfusion.lang.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.LSFGroupExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.impl.LSFExplicitInterfacePropertyStatementImpl;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.impl.ExplicitInterfaceStubImpl;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementType;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class ExplicitInterfaceStubElementType extends LSFStubElementType<ExplicitInterfaceStubElement, LSFExplicitInterfacePropStatement> {
    public ExplicitInterfaceStubElementType() {
        super("EXPLICIT_INTERFACE_PROPERTY_STATEMENT");
    }

    @Override
    public LSFExplicitInterfacePropStatement createPsi(@NotNull ExplicitInterfaceStubElement stub) {
        return new LSFExplicitInterfacePropertyStatementImpl(stub, this);
    }

    @Override
    public ExplicitInterfaceStubElement createStub(@NotNull final LSFExplicitInterfacePropStatement psi, StubElement parentStub) {
        return new ExplicitInterfaceStubImpl(parentStub, psi);
    }

    @Override
    public void serialize(@NotNull ExplicitInterfaceStubElement stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getDeclName());

        LSFExplicitClasses params = stub.getParamExplicitClasses();
        dataStream.writeBoolean(params != null);
        if (params != null) {
            params.serialize(dataStream);
        }

        Set<String> values = stub.getParamExplicitValues();
        dataStream.writeBoolean(values != null);
        if (values != null) {
            LSFGroupExplicitClasses.serializeSet(dataStream, values);
        }

        dataStream.writeByte(stub.getPropType());
    }

    @NotNull
    @Override
    public ExplicitInterfaceStubElement deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        return new ExplicitInterfaceStubImpl(parentStub, this, name != null ? name.getString() : null, dataStream.readBoolean() ? LSFExplicitClasses.deserialize(dataStream) : null, dataStream.readBoolean() ? LSFGroupExplicitClasses.deserializeSet(dataStream) : null, dataStream.readByte());
    }

    @Override
    public void indexStub(@NotNull ExplicitInterfaceStubElement stub, @NotNull IndexSink sink) {
        LSFExplicitClasses paramExplicitClasses = stub.getParamExplicitClasses();
        if(paramExplicitClasses != null) {
            Set<String> set = new HashSet<String>(); // избегаем повторного добавления при многократном вхождении класса
            for (String paramClass : paramExplicitClasses.getIndexedClasses()) {
                if (paramClass != null && set.add(paramClass))
                    sink.occurrence(LSFIndexKeys.EXPLICIT_INTERFACE, paramClass);
            }
        }
    }
}
