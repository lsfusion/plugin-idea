package com.lsfusion.lang.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.LSFPsiImplUtil;
import com.lsfusion.lang.psi.declarations.LSFImplicitValuePropStatement;
import com.lsfusion.lang.psi.impl.LSFImplicitValuePropertyStatementImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.stubs.interfaces.ImplicitValueStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.impl.ImplicitValueStubImpl;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementType;
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

    @NotNull
    @Override
    public ImplicitValueStubElement deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ImplicitValueStubImpl(dataStream, parentStub, this);
    }

    @Override
    public void indexStub(@NotNull ImplicitValueStubElement stub, @NotNull IndexSink sink) {
        List<String> valueProps = stub.getValueProperties();
        if (valueProps != null) {
            Set<String> set = new HashSet<>(valueProps); // избегаем повторного добавления при многократном вхождении свойства
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
        final ImplicitValueStubImpl stub = new ImplicitValueStubImpl(parentStub, psi);

        LSFPropertyStatement propertyStatement = psi.getPropertyStatement();
        List<String> propNames = LSFPsiImplUtil.getValueAPPropertyNames(propertyStatement.getPropertyCalcStatement());
        stub.setValueProperties(propNames);

        return stub;
    }
}
