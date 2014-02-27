package com.lsfusion.lang.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.psi.LSFExpressionUnfriendlyPD;
import com.lsfusion.lang.psi.LSFPropertyExpression;
import com.lsfusion.lang.psi.LSFPsiImplUtil;
import com.lsfusion.lang.psi.declarations.LSFExplicitValuePropStatement;
import com.lsfusion.lang.psi.impl.LSFExplicitValuePropertyStatementImpl;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitValueStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.impl.ExplicitValueStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExplicitValueStubElementType extends IStubElementType<ExplicitValueStubElement, LSFExplicitValuePropStatement> {
    public final StubIndexKey<String, LSFExplicitValuePropStatement> key;

    public ExplicitValueStubElementType() {
        super("EXPLICIT_VALUE_PROPERTY_STATEMENT", LSFLanguage.INSTANCE);
        key = StubIndexKey.createIndexKey(getExternalId());
    }

    @Override
    public String getExternalId() {
        return "lsf.ExplicitValue";
    }

    @Override
    public void serialize(@NotNull ExplicitValueStubElement stub, @NotNull StubOutputStream dataStream) throws IOException {
        List<String> classes = stub.getValueClasses();
        if (classes != null) {
            dataStream.writeInt(classes.size());
            for (int i = 0; i < classes.size(); i++) {
                dataStream.writeName(classes.get(i));
            }
        } else {
            dataStream.writeInt(0);
        }
    }

    @NotNull
    @Override
    public ExplicitValueStubElement deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        int classCount = dataStream.readInt();
        List<String> classes = new ArrayList<String>();
        if (classCount != 0) {
            for (int i = 0; i < classCount; i++) {
                StringRef name = dataStream.readName();
                classes.add(name != null ? name.getString() : null);
            }
        }
        ExplicitValueStubImpl stub = new ExplicitValueStubImpl(parentStub, this, classes);

        return stub;
    }

    @Override
    public void indexStub(@NotNull ExplicitValueStubElement stub, @NotNull IndexSink sink) {
        List<String> valueClasses = stub.getValueClasses();
        if (valueClasses != null) {
            Set<String> set = new HashSet<String>(valueClasses); // избегаем повторного добавления при многократном вхождении класса
            for (String valueClass : set) {
                if (valueClass != null) {
                    sink.occurrence(key, valueClass);
                }
            }
        }
    }

    @Override
    public LSFExplicitValuePropStatement createPsi(@NotNull ExplicitValueStubElement stub) {
        return new LSFExplicitValuePropertyStatementImpl(stub, this);
    }

    @Override
    public ExplicitValueStubElement createStub(@NotNull LSFExplicitValuePropStatement psi, StubElement parentStub) {
        final ExplicitValueStubImpl stub = new ExplicitValueStubImpl(parentStub, psi.getElementType());

        List<String> classNames = new ArrayList<String>();
        LSFExpressionUnfriendlyPD expressionUnfriendlyPD = psi.getPropertyStatement().getExpressionUnfriendlyPD();
        if (expressionUnfriendlyPD != null) {
            classNames.addAll(LSFPsiImplUtil.getValueClassNames(expressionUnfriendlyPD));
        } else {
            LSFPropertyExpression propertyExpression = psi.getPropertyStatement().getPropertyExpression();
            if (propertyExpression != null) {
                classNames.addAll(LSFPsiImplUtil.getValueClassNames(propertyExpression));
            }
        }

        stub.setValueClasses(classNames);

        return stub;
    }
}
