package com.lsfusion.lang.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.LSFContextIndependentPD;
import com.lsfusion.lang.psi.LSFExpressionUnfriendlyPD;
import com.lsfusion.lang.psi.context.UnfriendlyPE;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;
import com.lsfusion.lang.psi.declarations.impl.LSFGlobalPropDeclarationImpl;
import com.lsfusion.lang.psi.impl.LSFExplicitInterfacePropertyStatementImpl;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.impl.ExplicitInterfaceStubImpl;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementType;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExplicitInterfaceStubElementType extends LSFStubElementType<ExplicitInterfaceStubElement, LSFExplicitInterfacePropStatement> {
    public ExplicitInterfaceStubElementType() {
        super("EXPLICIT_INTERFACE_PROPERTY_STATEMENT");
    }

    @Override
    public LSFExplicitInterfacePropStatement createPsi(@NotNull ExplicitInterfaceStubElement stub) {
        return new LSFExplicitInterfacePropertyStatementImpl(stub, this);
    }

    @Override
    public ExplicitInterfaceStubElement createStub(@NotNull final LSFExplicitInterfacePropStatement psi, StubElement parentStub) {
        final ExplicitInterfaceStubImpl stub = new ExplicitInterfaceStubImpl(parentStub, psi.getElementType());

        List<String> classNames = new ArrayList<String>();
        List<LSFParamDeclaration> explicitParams = (List<LSFParamDeclaration>) ((LSFGlobalPropDeclarationImpl) psi.getPropertyStatement()).getExplicitParams();
        if (explicitParams != null) {
            for (LSFParamDeclaration param : explicitParams) {
                String className = param.getClassName();
                if (className != null) {
                    classNames.add(className);
                }
            }
        }
        if (classNames.isEmpty()) {
            LSFExpressionUnfriendlyPD expressionUnfriendlyPD = psi.getPropertyStatement().getExpressionUnfriendlyPD();
            if (expressionUnfriendlyPD != null) {
                LSFContextIndependentPD contextIndependentPD = expressionUnfriendlyPD.getContextIndependentPD();
                if (contextIndependentPD != null) {
                    classNames = ((UnfriendlyPE) contextIndependentPD.getFirstChild()).getValueParamClassNames();
                }
            }
        }

        stub.setParamClasses(classNames);

        return stub;
    }

    @Override
    public void serialize(@NotNull ExplicitInterfaceStubElement stub, @NotNull StubOutputStream dataStream) throws IOException {
        List<String> params = stub.getParamClasses();
        if (params != null) {
            dataStream.writeInt(params.size());
            for (String param : params) {
                dataStream.writeName(param);
            }
        } else {
            dataStream.writeInt(0);
        }
    }

    @NotNull
    @Override
    public ExplicitInterfaceStubElement deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        int paramsCount = dataStream.readInt();
        List<String> params = new ArrayList<String>();
        if (paramsCount != 0) {
            for (int i = 0; i < paramsCount; i++) {
                StringRef name = dataStream.readName();
                params.add(name != null ? name.getString() : null);
            }
        }

        return new ExplicitInterfaceStubImpl(parentStub, this, params);
    }

    @Override
    public void indexStub(@NotNull ExplicitInterfaceStubElement stub, @NotNull IndexSink sink) {
        List<String> paramClasses = stub.getParamClasses();
        if (paramClasses != null) {
            Set<String> set = new HashSet<String>(paramClasses); // избегаем повторного добавления при многократном вхождении класса
            for (String paramClass : set) {
                if (paramClass != null) {
                    sink.occurrence(LSFIndexKeys.EXPLICIT_INTERFACE, paramClass);
                }
            }
        }
    }
}
