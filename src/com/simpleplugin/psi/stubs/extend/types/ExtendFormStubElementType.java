package com.simpleplugin.psi.stubs.extend.types;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.simpleplugin.psi.extend.LSFFormExtend;
import com.simpleplugin.psi.impl.LSFFormStatementImpl;
import com.simpleplugin.psi.stubs.extend.ExtendFormStubElement;
import com.simpleplugin.psi.stubs.extend.impl.ExtendFormStubImpl;
import com.simpleplugin.psi.stubs.extend.types.indexes.ExtendFormIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExtendFormStubElementType extends ExtendStubElementType<LSFFormExtend, ExtendFormStubElement> {

    public ExtendFormStubElementType() {
        super("EXTEND_FORM");
    }

    @Override
    public StringStubIndexExtension<LSFFormExtend> getGlobalIndex() {
        return ExtendFormIndex.getInstance();
    }

    @Override
    public LSFFormExtend createPsi(@NotNull ExtendFormStubElement stub) {
        return new LSFFormStatementImpl(stub, this);
    }

    @Override
    public ExtendFormStubElement createStub(@NotNull LSFFormExtend psi, StubElement parentStub) {
        return new ExtendFormStubImpl(parentStub, psi);
    }

    @Override
    public ExtendFormStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ExtendFormStubImpl(dataStream, parentStub, this);
    }
}
