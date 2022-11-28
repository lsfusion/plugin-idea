package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.LSFImplicitExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFBaseEventParamGlobalPropDeclaration;
import com.lsfusion.lang.psi.stubs.BaseEventParamPropStubElement;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Set;

public class BaseEventParamPropStubImpl extends PropStubImpl<BaseEventParamPropStubElement, LSFBaseEventParamGlobalPropDeclaration> implements BaseEventParamPropStubElement {

    private LSFExplicitClasses paramClasses;

    private Set<String> valueClasses;

    public BaseEventParamPropStubImpl(StubElement parent, LSFBaseEventParamGlobalPropDeclaration psi) {
        super(parent, psi);
        paramClasses = psi.getExplicitParams();
        valueClasses = psi.getExplicitValues();
    }

    public BaseEventParamPropStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<BaseEventParamPropStubElement, LSFBaseEventParamGlobalPropDeclaration> type) throws IOException {
        super(dataStream, parentStub, type);

        paramClasses  = dataStream.readBoolean() ? LSFExplicitClasses.deserialize(dataStream) : null;
        valueClasses = dataStream.readBoolean() ? LSFImplicitExplicitClasses.deserializeSet(dataStream) : null;
    }

    @Override
    public void serialize(StubOutputStream dataStream) throws IOException {
        super.serialize(dataStream);

        LSFExplicitClasses params = getParamExplicitClasses();
        dataStream.writeBoolean(params != null);
        if (params != null) {
            params.serialize(dataStream);
        }

        Set<String> values = getParamExplicitValues();
        dataStream.writeBoolean(values != null);
        if (values != null) {
            LSFImplicitExplicitClasses.serializeSet(dataStream, values);
        }
    }

    @Override
    @Nullable
    public LSFExplicitClasses getParamExplicitClasses() {
        return paramClasses;
    }

    @Override
    public Set<String> getParamExplicitValues() {
        return valueClasses;
    }
}
