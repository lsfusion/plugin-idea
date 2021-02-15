package com.lsfusion.lang.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFImplicitInterfacePropStatement;
import com.lsfusion.lang.psi.impl.LSFImplicitInterfacePropertyStatementImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.stubs.interfaces.ImplicitInterfaceStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.impl.ImplicitInterfaceStubImpl;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImplicitInterfaceStubElementType extends LSFStubElementType<ImplicitInterfaceStubElement, LSFImplicitInterfacePropStatement> {
    public ImplicitInterfaceStubElementType() {
        super("IMPLICIT_INTERFACE_PROPERTY_STATEMENT");
    }

    @Override
    public LSFImplicitInterfacePropStatement createPsi(@NotNull ImplicitInterfaceStubElement stub) {
        return new LSFImplicitInterfacePropertyStatementImpl(stub, this);
    }

    @Override
    public ImplicitInterfaceStubElement createStub(@NotNull final LSFImplicitInterfacePropStatement psi, StubElement parentStub) {
        final ImplicitInterfaceStubImpl stub = new ImplicitInterfaceStubImpl(parentStub, psi);

        LSFPropertyStatement propertyStatement = psi.getPropertyStatement();
        LSFPropertyCalcStatement pCalcStatement = propertyStatement.getPropertyCalcStatement();
        if (pCalcStatement != null) {
            LSFExpressionUnfriendlyPD contextIndependentPD = pCalcStatement.getExpressionUnfriendlyPD();
            if (contextIndependentPD != null) {
                LSFGroupPropertyDefinition groupPropertyDefinition = contextIndependentPD.getGroupPropertyDefinition();
                if (groupPropertyDefinition != null) {
                    LSFGroupPropertyBy groupPropertyBy = groupPropertyDefinition.getGroupPropertyBy();
                    if (groupPropertyBy != null) {
                        List<String> props = new ArrayList<>();
                        LSFNonEmptyPropertyExpressionList neList = groupPropertyBy.getNonEmptyPropertyExpressionList();
                        if(neList != null) {
                            for (LSFPropertyExpression pe : neList.getPropertyExpressionList()) {
                                props.addAll(LSFPsiImplUtil.getValuePropertyNames(pe));
                            }
                        }
                        stub.setParamProperties(props);
                    }
                }
            }
        }

        return stub;
    }

    @NotNull
    @Override
    public ImplicitInterfaceStubElement deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ImplicitInterfaceStubImpl(dataStream, parentStub, this);
    }

    @Override
    public void indexStub(@NotNull ImplicitInterfaceStubElement stub, @NotNull IndexSink sink) {
        List<String> paramProps = stub.getParamProperties();
        if (paramProps != null) {
            Set<String> set = new HashSet<>(paramProps); // избегаем повторного добавления при многократном вхождении свойства
            for (String paramClass : set) {
                if (paramClass != null) {
                    sink.occurrence(LSFIndexKeys.IMPLICIT_INTERFACE, paramClass);
                }
            }
        }
    }
}
