package com.lsfusion.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.lsfusion.LSFLanguage;
import com.lsfusion.psi.LSFPsiImplUtil;
import com.lsfusion.psi.*;
import com.lsfusion.psi.declarations.LSFImplicitInterfacePropStatement;
import com.lsfusion.psi.impl.LSFImplicitInterfacePropertyStatementImpl;
import com.lsfusion.psi.stubs.interfaces.ImplicitInterfaceStubElement;
import com.lsfusion.psi.stubs.interfaces.impl.ImplicitInterfaceStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImplicitInterfaceStubElementType extends IStubElementType<ImplicitInterfaceStubElement, LSFImplicitInterfacePropStatement> {
    public final StubIndexKey<String, LSFImplicitInterfacePropStatement> key;

    public ImplicitInterfaceStubElementType() {
        super("IMPLICIT_INTERFACE_PROPERTY_STATEMENT", LSFLanguage.INSTANCE);
        key = StubIndexKey.createIndexKey(getExternalId());
    }

    @Override
    public LSFImplicitInterfacePropStatement createPsi(@NotNull ImplicitInterfaceStubElement stub) {
        return new LSFImplicitInterfacePropertyStatementImpl(stub, this);
    }

    @Override
    public ImplicitInterfaceStubElement createStub(@NotNull final LSFImplicitInterfacePropStatement psi, StubElement parentStub) {
        final ImplicitInterfaceStubImpl stub = new ImplicitInterfaceStubImpl(parentStub, psi.getElementType());

        LSFExpressionUnfriendlyPD expressionUnfriendlyPD = psi.getPropertyStatement().getExpressionUnfriendlyPD();
        if (expressionUnfriendlyPD != null) {
            LSFContextIndependentPD contextIndependentPD = expressionUnfriendlyPD.getContextIndependentPD();
            if (contextIndependentPD != null) {
                LSFGroupPropertyDefinition groupPropertyDefinition = contextIndependentPD.getGroupPropertyDefinition();
                if (groupPropertyDefinition != null) {
                    LSFGroupPropertyBy groupPropertyBy = groupPropertyDefinition.getGroupPropertyBy();
                    if (groupPropertyBy != null) {
                        List<String> props = new ArrayList<String>();
                        for (LSFPropertyExpression pe : groupPropertyBy.getNonEmptyPropertyExpressionList().getPropertyExpressionList()) {
                            props.addAll(LSFPsiImplUtil.getValuePropertyNames(pe));
                        }
                        stub.setParamProperties(props);
                    }
                }
            }
        }

        return stub;
    }

    @Override
    public String getExternalId() {
        return "lsf.ImplicitInterface";
    }

    @Override
    public void serialize(ImplicitInterfaceStubElement stub, StubOutputStream dataStream) throws IOException {
        List<String> params = stub.getParamProperties();
        if (params != null) {
            dataStream.writeInt(params.size());
            for (int i = 0; i < params.size(); i++) {
                dataStream.writeName(params.get(i));
            }
        } else {
            dataStream.writeInt(0);
        }
    }

    @Override
    public ImplicitInterfaceStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        int paramsCount = dataStream.readInt();
        List<String> params = new ArrayList<String>();
        if (paramsCount != 0) {
            for (int i = 0; i < paramsCount; i++) {
                StringRef name = dataStream.readName();
                params.add(name != null ? name.getString() : null);
            }
        }
        ImplicitInterfaceStubImpl stub = new ImplicitInterfaceStubImpl(parentStub, this, params);

        return stub;
    }

    @Override
    public void indexStub(ImplicitInterfaceStubElement stub, IndexSink sink) {
        List<String> paramProps = stub.getParamProperties();
        if (paramProps != null) {
            Set<String> set = new HashSet<String>(paramProps); // избегаем повторного добавления при многократном вхождении свойства
            for (String paramClass : set) {
                if (paramClass != null) {
                    sink.occurrence(key, paramClass);
                }
            }
        }
    }
}
