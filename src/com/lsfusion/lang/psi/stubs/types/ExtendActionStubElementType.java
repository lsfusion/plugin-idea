package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFActionExtend;
import com.lsfusion.lang.psi.impl.LSFOverrideActionStatementImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.indexes.LSFStringStubIndex;
import com.lsfusion.lang.psi.indexes.ExtendActionIndex;
import com.lsfusion.lang.psi.stubs.ExtendActionStubElement;
import com.lsfusion.lang.psi.stubs.impl.ExtendActionStubImpl;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendActionOrPropStubElementType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExtendActionStubElementType extends ExtendActionOrPropStubElementType<LSFActionExtend, ExtendActionStubElement> {

    public ExtendActionStubElementType() {
        super("EXTENDACTION");
    }

    @Override
    public LSFStringStubIndex<LSFActionExtend> getGlobalIndex() {
        return ExtendActionIndex.getInstance();
    }

    @Override
    public StubIndexKey<String, LSFActionExtend> getGlobalIndexKey() {
        return LSFIndexKeys.EXTENDACTION;
    }

    @Override
    public LSFActionExtend createPsi(@NotNull ExtendActionStubElement stub) {
        return new LSFOverrideActionStatementImpl(stub, this);
    }

    @Override
    public ExtendActionStubElement createStub(@NotNull LSFActionExtend psi, StubElement parentStub) {
        return new ExtendActionStubImpl(parentStub, psi);
    }

    @Override
    public ExtendActionStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ExtendActionStubImpl(dataStream, parentStub, this);
    }
}