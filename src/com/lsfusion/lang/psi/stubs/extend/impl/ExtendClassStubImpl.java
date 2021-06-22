package com.lsfusion.lang.psi.stubs.extend.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.lsfusion.lang.psi.LSFStringClassRef;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.stubs.extend.ExtendClassStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendClassStubElementType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExtendClassStubImpl extends ExtendStubImpl<LSFClassExtend, ExtendClassStubElement> implements ExtendClassStubElement {

    private LSFStringClassRef thisElement;
    private List<LSFStringClassRef> myExtends;

    @Override
    @NotNull
    public LSFStringClassRef getThis() {
        return thisElement;
    }

    @Override
    public List<LSFStringClassRef> getExtends() {
        return myExtends;
    }

    public ExtendClassStubImpl(StubElement parent, LSFClassExtend psi) {
        super(parent, psi);

        thisElement = psi.getThis();
        myExtends = psi.getExtends();
    }

    public ExtendClassStubImpl(StubInputStream dataStream, StubElement parentStub, ExtendClassStubElementType type) throws IOException {
        super(dataStream, parentStub, type);

        boolean isElementDeserialisable = dataStream.readBoolean();
        thisElement = isElementDeserialisable ? LSFStringClassRef.deserialize(dataStream) : null;
        myExtends = new ArrayList<>();
        for (int i = 0, size = dataStream.readInt(); i < size; i++) {
            myExtends.add(LSFStringClassRef.deserialize(dataStream));
        }
    }

    @Override
    public void serialize(StubOutputStream dataStream) throws IOException {
        super.serialize(dataStream);

        boolean isElementSerialisable = thisElement != null;
        dataStream.writeBoolean(isElementSerialisable);
        if (isElementSerialisable)
            thisElement.serialize(dataStream);

        dataStream.writeInt(myExtends.size());
        for (LSFStringClassRef shortExtend : myExtends) {
            shortExtend.serialize(dataStream);
        }
    }
}
