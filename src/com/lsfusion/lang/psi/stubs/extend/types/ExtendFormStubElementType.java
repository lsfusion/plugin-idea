package com.lsfusion.lang.psi.stubs.extend.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.impl.LSFFormStatementImpl;
import com.lsfusion.lang.psi.indexes.LSFStringStubIndex;
import com.lsfusion.lang.psi.stubs.extend.ExtendFormStubElement;
import com.lsfusion.lang.psi.stubs.extend.impl.ExtendFormStubImpl;
import com.lsfusion.lang.psi.indexes.ExtendFormIndex;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExtendFormStubElementType extends ExtendStubElementType<LSFFormExtend, ExtendFormStubElement> {

    public ExtendFormStubElementType() {
        super("EXTEND_FORM");
    }

    @Override
    public LSFStringStubIndex<LSFFormExtend> getGlobalIndex() {
        return ExtendFormIndex.getInstance();
    }

    @Override
    public StubIndexKey<String, LSFFormExtend> getGlobalIndexKey() {
        return LSFIndexKeys.EXTENDFORM;
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
