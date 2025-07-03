package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFOverrideActionDeclaration;
import com.lsfusion.lang.psi.impl.LSFOverrideActionStatementImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.indexes.LSFStringStubIndex;
import com.lsfusion.lang.psi.indexes.OverrideActionIndex;
import com.lsfusion.lang.psi.stubs.OverrideActionStubElement;
import com.lsfusion.lang.psi.stubs.impl.OverrideActionStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class OverrideActionStubElementType extends FullNameStubElementType<OverrideActionStubElement, LSFOverrideActionDeclaration> {

    public OverrideActionStubElementType() {
        super("OVERRIDEACTION");
    }

    @Override
    public LSFStringStubIndex<LSFOverrideActionDeclaration> getGlobalIndex() {
        return OverrideActionIndex.getInstance();
    }

    @Override
    public StubIndexKey<String, LSFOverrideActionDeclaration> getGlobalIndexKey() {
        return LSFIndexKeys.OVERRIDEACTION;
    }

    @Override
    public LSFOverrideActionDeclaration createPsi(@NotNull OverrideActionStubElement stub) {
        return new LSFOverrideActionStatementImpl(stub, this);
    }

    @Override
    public OverrideActionStubElement createStub(@NotNull LSFOverrideActionDeclaration psi, StubElement parentStub) {
        return new OverrideActionStubImpl(parentStub, psi);
    }

    @Override
    public OverrideActionStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new OverrideActionStubImpl(dataStream, parentStub, this);
    }
}