package com.lsfusion.lang.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.psi.LSFExpressionUnfriendlyPD;
import com.lsfusion.lang.psi.LSFPropertyExpression;
import com.lsfusion.lang.psi.LSFPsiImplUtil;
import com.lsfusion.lang.psi.declarations.LSFImplicitValuePropStatement;
import com.lsfusion.lang.psi.impl.LSFImplicitValuePropertyStatementImpl;
import com.lsfusion.lang.psi.stubs.interfaces.ImplicitValueStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.impl.ImplicitValueStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImplicitValueStubElementType extends IStubElementType<ImplicitValueStubElement, LSFImplicitValuePropStatement> {
    public final StubIndexKey<String, LSFImplicitValuePropStatement> key;

    public ImplicitValueStubElementType() {
        super("IMPLICIT_VALUE_PROPERTY_STATEMENT", LSFLanguage.INSTANCE);
        key = StubIndexKey.createIndexKey(getExternalId());
    }

    @Override
    public String getExternalId() {
        return "lsf.ImplicitValue";
    }

    @Override
    public void serialize(@NotNull ImplicitValueStubElement stub, @NotNull StubOutputStream dataStream) throws IOException {
        List<String> props = stub.getValueProperties();
        if (props != null) {
            dataStream.writeInt(props.size());
            for (int i = 0; i < props.size(); i++) {
                dataStream.writeName(props.get(i));
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
        ImplicitValueStubImpl stub = new ImplicitValueStubImpl(parentStub, this, props);

        return stub;
    }

    @Override
    public void indexStub(@NotNull ImplicitValueStubElement stub, @NotNull IndexSink sink) {
        List<String> valueProps = stub.getValueProperties();
        if (valueProps != null) {
            Set<String> set = new HashSet<String>(valueProps); // избегаем повторного добавления при многократном вхождении свойства
            for (String valueClass : set) {
                if (valueClass != null) {
                    sink.occurrence(key, valueClass);
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

        List<String> propNames = new ArrayList<String>();
        LSFExpressionUnfriendlyPD expressionUnfriendlyPD = psi.getPropertyStatement().getExpressionUnfriendlyPD();
        if (expressionUnfriendlyPD != null) {
            propNames.addAll(LSFPsiImplUtil.getValuePropertyNames(expressionUnfriendlyPD));
        } else {
            LSFPropertyExpression propertyExpression = psi.getPropertyStatement().getPropertyExpression();
            if (propertyExpression != null) {
                propNames.addAll(LSFPsiImplUtil.getValuePropertyNames(propertyExpression));
            }
        }
        stub.setValueProperties(propNames);

        return stub;
    }
}
