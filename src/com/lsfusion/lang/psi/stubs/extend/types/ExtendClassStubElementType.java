package com.lsfusion.lang.psi.stubs.extend.types;

import com.intellij.psi.stubs.*;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.impl.LSFClassStatementImpl;
import com.lsfusion.lang.psi.stubs.extend.ExtendClassStubElement;
import com.lsfusion.lang.psi.stubs.extend.impl.ExtendClassStubImpl;
import com.lsfusion.lang.psi.indexes.ExtendClassIndex;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExtendClassStubElementType extends ExtendStubElementType<LSFClassExtend, ExtendClassStubElement> {

    public ExtendClassStubElementType() {
        super("EXTEND_CLASS");
    }

    public StringStubIndexExtension<LSFClassExtend> getGlobalIndex() {
        return ExtendClassIndex.getInstance();
    }

    @Override
    public StubIndexKey<String, LSFClassExtend> getGlobalIndexKey() {
        return LSFIndexKeys.EXTENDCLASS;
    }

    @Override
    public LSFClassExtend createPsi(@NotNull ExtendClassStubElement stub) {
        return new LSFClassStatementImpl(stub, this);
    }

    @Override
    public ExtendClassStubElement createStub(@NotNull LSFClassExtend psi, StubElement parentStub) {
        return new ExtendClassStubImpl(parentStub, psi);
    }

    @Override
    public ExtendClassStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ExtendClassStubImpl(dataStream, parentStub, this);
    }

    @Override
    public void indexStub(@NotNull ExtendClassStubElement stub, @NotNull IndexSink sink) {
        super.indexStub(stub, sink);

        for (String shortExtend : stub.getShortExtends()) {
            sink.occurrence(LSFIndexKeys.EXTENDCLASS_SHORT, shortExtend);
        }
    }
}
