package com.lsfusion.lang.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFImplicitValuePropStatement;
import com.lsfusion.lang.psi.impl.LSFImplicitValuePropertyStatementImpl;
import com.lsfusion.lang.psi.stubs.interfaces.ImplicitValueStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.impl.ImplicitValueStubImpl;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementType;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImplicitValueStubElementType extends LSFStubElementType<ImplicitValueStubElement, LSFImplicitValuePropStatement> {
    public ImplicitValueStubElementType() {
        super("IMPLICIT_VALUE_PROPERTY_STATEMENT");
    }

    @Override
    public void serialize(@NotNull ImplicitValueStubElement stub, @NotNull StubOutputStream dataStream) throws IOException {
        List<String> props = stub.getValueProperties();
        if (props != null) {
            dataStream.writeInt(props.size());
            for (String prop : props) {
                dataStream.writeName(prop);
            }
        } else {
            dataStream.writeInt(0);
        }
    }

    @NotNull
    @Override
    public ImplicitValueStubElement deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        int propsCount = dataStream.readInt();
        List<String> props = new ArrayList<String>();
        if (propsCount != 0) {
            for (int i = 0; i < propsCount; i++) {
                StringRef name = dataStream.readName();
                props.add(name != null ? name.getString() : null);
            }
        }

        return new ImplicitValueStubImpl(parentStub, this, props);
    }

    @Override
    public void indexStub(@NotNull ImplicitValueStubElement stub, @NotNull IndexSink sink) {
        List<String> valueProps = stub.getValueProperties();
        if (valueProps != null) {
            Set<String> set = new HashSet<String>(valueProps); // избегаем повторного добавления при многократном вхождении свойства
            for (String valueClass : set) {
                if (valueClass != null) {
                    sink.occurrence(LSFIndexKeys.IMPLICIT_VALUE, valueClass);
                }
            }
        }
    }

    @Override
    public LSFImplicitValuePropStatement createPsi(@NotNull ImplicitValueStubElement stub) {
        return new LSFImplicitValuePropertyStatementImpl(stub, this);
    }

    @Override
    public ImplicitValueStubElement createStub(@NotNull LSFImplicitValuePropStatement psi, StubElement parentStub) {
        final ImplicitValueStubImpl stub = new ImplicitValueStubImpl(parentStub, psi.getElementType());

        LSFPropertyStatement propertyStatement = psi.getPropertyStatement();
        List<String> propNames = LSFPsiImplUtil.getValueAPPropertyNames(propertyStatement.getPropertyCalcStatement(), propertyStatement.getActionStatement());
        stub.setValueProperties(propNames);

        return stub;
    }
}
