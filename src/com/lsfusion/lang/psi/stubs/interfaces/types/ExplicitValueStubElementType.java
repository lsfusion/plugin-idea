package com.lsfusion.lang.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.LSFExpressionUnfriendlyPD;
import com.lsfusion.lang.psi.LSFPropertyExpression;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.LSFPsiImplUtil;
import com.lsfusion.lang.psi.declarations.LSFExplicitValuePropStatement;
import com.lsfusion.lang.psi.impl.LSFExplicitValuePropertyStatementImpl;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitValueStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.impl.ExplicitValueStubImpl;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementType;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExplicitValueStubElementType extends LSFStubElementType<ExplicitValueStubElement, LSFExplicitValuePropStatement> {
    public ExplicitValueStubElementType() {
        super("EXPLICIT_VALUE_PROPERTY_STATEMENT");
    }

    @Override
    public void serialize(@NotNull ExplicitValueStubElement stub, @NotNull StubOutputStream dataStream) throws IOException {
        List<String> classes = stub.getValueClasses();
        if (classes != null) {
            dataStream.writeInt(classes.size());
            for (String aClass : classes) {
                dataStream.writeName(aClass);
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

        return new ExplicitValueStubImpl(parentStub, this, classes);
    }

    @Override
    public void indexStub(@NotNull ExplicitValueStubElement stub, @NotNull IndexSink sink) {
        List<String> valueClasses = stub.getValueClasses();
        if (valueClasses != null) {
            Set<String> set = new HashSet<String>(valueClasses); // избегаем повторного добавления при многократном вхождении класса
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
        final ExplicitValueStubImpl stub = new ExplicitValueStubImpl(parentStub, psi.getElementType());

        List<String> classNames = new ArrayList<String>();
        LSFPropertyStatement propertyStatement = psi.getPropertyStatement();

        LSFExpressionUnfriendlyPD expressionUnfriendlyPD = propertyStatement.getExpressionUnfriendlyPD();
        if (expressionUnfriendlyPD != null) {
            classNames.addAll(LSFPsiImplUtil.getValueClassNames(expressionUnfriendlyPD));
        } else {
            LSFPropertyExpression propertyExpression = propertyStatement.getPropertyExpression();

            assert propertyExpression != null;

            classNames.addAll(LSFPsiImplUtil.getValueClassNames(propertyExpression));
        }

        stub.setValueClasses(classNames);

        return stub;
    }
}
