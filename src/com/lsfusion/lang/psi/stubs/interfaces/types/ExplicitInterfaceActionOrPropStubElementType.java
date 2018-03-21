package com.lsfusion.lang.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.LSFImplicitExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionOrPropStatement;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceActionOrPropStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.impl.ExplicitInterfacePropStubImpl;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class ExplicitInterfaceActionOrPropStubElementType<Stub extends ExplicitInterfaceActionOrPropStubElement<Psi>, Psi extends LSFExplicitInterfaceActionOrPropStatement<Stub>> extends LSFStubElementType<Stub, Psi> {
    public ExplicitInterfaceActionOrPropStubElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public void serialize(@NotNull Stub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getDeclName());
        
        LSFExplicitClasses params = stub.getParamExplicitClasses();
        dataStream.writeBoolean(params != null);
        if (params != null) {
            params.serialize(dataStream);
        }

        dataStream.writeByte(stub.getPropType());
    }

    @NotNull
    @Override
    public Stub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef nameRef = dataStream.readName();
        String name = nameRef != null ? nameRef.getString() : null;
        LSFExplicitClasses params = dataStream.readBoolean() ? LSFExplicitClasses.deserialize(dataStream) : null;
        byte propType = dataStream.readByte();
        return deserialize(parentStub, name, params, propType, dataStream);
    }

    protected abstract Stub deserialize(StubElement parentStub, String name, LSFExplicitClasses params, byte propType, @NotNull StubInputStream dataStream) throws IOException;

    protected abstract StubIndexKey getIndexKey();
    
    @Override
    public void indexStub(@NotNull Stub stub, @NotNull IndexSink sink) {
        LSFExplicitClasses paramExplicitClasses = stub.getParamExplicitClasses();
        if(paramExplicitClasses != null) {
            Set<String> set = new HashSet<>(); // избегаем повторного добавления при многократном вхождении класса
            for (String paramClass : paramExplicitClasses.getIndexedClasses()) {
                if (paramClass != null && set.add(paramClass))
                    sink.occurrence(getIndexKey(), paramClass);
            }
        }
    }

}
