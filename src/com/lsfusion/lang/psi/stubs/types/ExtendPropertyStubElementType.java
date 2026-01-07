package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFPropertyExtend;
import com.lsfusion.lang.psi.impl.LSFOverridePropertyStatementImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.indexes.LSFStringStubIndex;
import com.lsfusion.lang.psi.indexes.ExtendPropertyIndex;
import com.lsfusion.lang.psi.stubs.ExtendPropertyStubElement;
import com.lsfusion.lang.psi.stubs.impl.ExtendPropertyStubImpl;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendActionOrPropStubElementType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExtendPropertyStubElementType extends ExtendActionOrPropStubElementType<LSFPropertyExtend, ExtendPropertyStubElement> {

    public ExtendPropertyStubElementType() {
        super("EXTENDPROPERTY");
    }

    @Override
    public LSFStringStubIndex<LSFPropertyExtend> getGlobalIndex() {
        return ExtendPropertyIndex.getInstance();
    }

    @Override
    public StubIndexKey<String, LSFPropertyExtend> getGlobalIndexKey() {
        return LSFIndexKeys.EXTENDPROPERTY;
    }

    @Override
    public LSFPropertyExtend createPsi(@NotNull ExtendPropertyStubElement stub) {
        return new LSFOverridePropertyStatementImpl(stub, this);
    }

    @Override
    public ExtendPropertyStubElement createStub(@NotNull LSFPropertyExtend psi, StubElement parentStub) {
        return new ExtendPropertyStubImpl(parentStub, psi);
    }

    @Override
    public ExtendPropertyStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ExtendPropertyStubImpl(dataStream, parentStub, this);
    }
}