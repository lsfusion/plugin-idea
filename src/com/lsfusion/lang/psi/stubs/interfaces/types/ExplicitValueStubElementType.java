package com.lsfusion.lang.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.LSFPsiImplUtil;
import com.lsfusion.lang.psi.declarations.LSFExplicitValuePropStatement;
import com.lsfusion.lang.psi.impl.LSFExplicitValuePropertyStatementImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitValueStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.impl.ExplicitValueStubImpl;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExplicitValueStubElementType extends LSFStubElementType<ExplicitValueStubElement, LSFExplicitValuePropStatement> {
    public ExplicitValueStubElementType() {
        super("EXPLICIT_VALUE_PROPERTY_STATEMENT");
    }

    @NotNull
    @Override
    public ExplicitValueStubElement deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ExplicitValueStubImpl(dataStream, parentStub, this);
    }

    @Override
    public void indexStub(@NotNull ExplicitValueStubElement stub, @NotNull IndexSink sink) {
        List<String> valueClasses = stub.getValueClasses();
        if (valueClasses != null) {
            Set<String> set = new HashSet<>(valueClasses); // избегаем повторного добавления при многократном вхождении класса
            for (String valueClass : set) {
                if (valueClass != null) {
                    sink.occurrence(LSFIndexKeys.EXPLICIT_VALUE, valueClass);
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
        final ExplicitValueStubImpl stub = new ExplicitValueStubImpl(parentStub, psi);

        LSFPropertyStatement propertyStatement = psi.getPropertyStatement();
        List<String> classNames = LSFPsiImplUtil.getValueAPClassNames(propertyStatement.getPropertyCalcStatement());
        stub.setValueClasses(classNames);

        return stub;
    }
}
