package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.lsfusion.lang.psi.declarations.LSFComponentStubDeclaration;
import com.lsfusion.lang.psi.impl.LSFComponentStubDeclImpl;
import com.lsfusion.lang.psi.stubs.ComponentStubElement;
import com.lsfusion.lang.psi.stubs.impl.ComponentStubImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ComponentStubElementType extends LSFStubElementType<ComponentStubElement, LSFComponentStubDeclaration> {
    public ComponentStubElementType() {
        super("COMPONENT_STUB_DECLARATION");
    }

    @Override
    public LSFComponentStubDeclaration createPsi(@NotNull ComponentStubElement stub) {
        return new LSFComponentStubDeclImpl(stub, this);
    }

    @Override
    public ComponentStubElement createStub(@NotNull LSFComponentStubDeclaration psi, StubElement parentStub) {
        String name = psi.getComponentDecl().getName();
        return new ComponentStubImpl(parentStub, this, name);
    }

    @Override
    public void serialize(@NotNull ComponentStubElement stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(((ComponentStubImpl) stub).name);
    }

    @NotNull
    @Override
    public ComponentStubElement deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ComponentStubImpl(parentStub, this, dataStream.readName().getString());
    }

    @Override
    public void indexStub(@NotNull ComponentStubElement stub, @NotNull IndexSink sink) {
//        sink.occurrence(LSFIndexKeys.COMPONENT, ((ComponentStubImpl) stub).name);
    }
}
