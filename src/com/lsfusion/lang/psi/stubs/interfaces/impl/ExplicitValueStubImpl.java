package com.lsfusion.lang.psi.stubs.interfaces.impl;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.declarations.LSFExplicitValuePropStatement;
import com.lsfusion.lang.psi.stubs.impl.ElementStubImpl;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitValueStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.types.ExplicitValueStubElementType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExplicitValueStubImpl extends ElementStubImpl<ExplicitValueStubElement, LSFExplicitValuePropStatement> implements ExplicitValueStubElement {
    private List<String> valueClasses;

    public ExplicitValueStubImpl(StubElement parent, LSFExplicitValuePropStatement psi) {
        super(parent, psi);
    }

    public ExplicitValueStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<ExplicitValueStubElement, LSFExplicitValuePropStatement> type) throws IOException {
        super(dataStream, parentStub, type);

        int classCount = dataStream.readInt();
        List<String> classes = new ArrayList<>();
        if (classCount != 0) {
            for (int i = 0; i < classCount; i++) {
                StringRef name = dataStream.readName();
                classes.add(name != null ? name.getString() : null);
            }
        }
        this.valueClasses = classes;
    }

    @Override
    public void serialize(StubOutputStream dataStream) throws IOException {
        super.serialize(dataStream);

        List<String> classes = valueClasses;
        if (classes != null) {
            dataStream.writeInt(classes.size());
            for (String aClass : classes) {
                dataStream.writeName(aClass);
            }
        } else {
            dataStream.writeInt(0);
        }
    }

    @Override
    public List<String> getValueClasses() {
        return valueClasses;
    }

    public void setValueClasses(List<String> valueClasses) {
        this.valueClasses = valueClasses;
    }
}
