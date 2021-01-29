package com.lsfusion.lang.psi.stubs.interfaces.impl;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.declarations.LSFImplicitValuePropStatement;
import com.lsfusion.lang.psi.stubs.impl.ElementStubImpl;
import com.lsfusion.lang.psi.stubs.interfaces.ImplicitValueStubElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImplicitValueStubImpl extends ElementStubImpl<ImplicitValueStubElement, LSFImplicitValuePropStatement> implements ImplicitValueStubElement {
    private List<String> valueProperties;

    public ImplicitValueStubImpl(StubElement parent, IStubElementType elementType) {
        super(parent, elementType);
    }

    public ImplicitValueStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<ImplicitValueStubElement, LSFImplicitValuePropStatement> type) throws IOException {
        super(dataStream, parentStub, type);

        int propsCount = dataStream.readInt();
        List<String> props = new ArrayList<>();
        if (propsCount != 0) {
            for (int i = 0; i < propsCount; i++) {
                StringRef name = dataStream.readName();
                props.add(name != null ? name.getString() : null);
            }
        }
        this.valueProperties = props;
    }

    @Override
    public void serialize(StubOutputStream dataStream) throws IOException {
        super.serialize(dataStream);

        List<String> props = valueProperties;
        if (props != null) {
            dataStream.writeInt(props.size());
            for (String prop : props) {
                dataStream.writeName(prop);
            }
        } else {
            dataStream.writeInt(0);
        }
    }

    @Override
    public List<String> getValueProperties() {
        return valueProperties;
    }

    public void setValueProperties(List<String> valueProperties) {
        this.valueProperties = valueProperties;
    }
}
