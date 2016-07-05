package com.lsfusion.lang.psi.stubs.extend.types;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.extend.LSFDesign;
import com.lsfusion.lang.psi.impl.LSFDesignStatementImpl;
import com.lsfusion.lang.psi.indexes.DesignIndex;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.stubs.extend.DesignStubElement;
import com.lsfusion.lang.psi.stubs.extend.impl.DesignStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class DesignStubElementType extends ExtendStubElementType<LSFDesign, DesignStubElement> {

    public DesignStubElementType() {
        super("EXTEND_DESIGN");
    }

    @Override
    public StringStubIndexExtension<LSFDesign> getGlobalIndex() {
        return DesignIndex.getInstance();
    }

    @Override
    public StubIndexKey<String, LSFDesign> getGlobalIndexKey() {
        return LSFIndexKeys.DESIGN;
    }

    @Override
    public LSFDesign createPsi(@NotNull DesignStubElement stub) {
        return new LSFDesignStatementImpl(stub, this);
    }

    @Override
    public DesignStubElement createStub(@NotNull LSFDesign psi, StubElement parentStub) {
        return new DesignStubImpl(parentStub, psi);
    }

    @Override
    public DesignStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new DesignStubImpl(dataStream, parentStub, this);
    }
}