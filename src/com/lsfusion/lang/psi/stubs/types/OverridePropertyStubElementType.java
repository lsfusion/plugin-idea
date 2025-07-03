package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFOverridePropertyDeclaration;
import com.lsfusion.lang.psi.impl.LSFOverridePropertyStatementImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.indexes.LSFStringStubIndex;
import com.lsfusion.lang.psi.indexes.OverridePropertyIndex;
import com.lsfusion.lang.psi.stubs.OverridePropertyStubElement;
import com.lsfusion.lang.psi.stubs.impl.OverridePropertyStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class OverridePropertyStubElementType extends FullNameStubElementType<OverridePropertyStubElement, LSFOverridePropertyDeclaration> {

    public OverridePropertyStubElementType() {
        super("OVERRIDEPROPERTY");
    }

    @Override
    public LSFStringStubIndex<LSFOverridePropertyDeclaration> getGlobalIndex() {
        return OverridePropertyIndex.getInstance();
    }

    @Override
    public StubIndexKey<String, LSFOverridePropertyDeclaration> getGlobalIndexKey() {
        return LSFIndexKeys.OVERRIDEPROPERTY;
    }

    @Override
    public LSFOverridePropertyDeclaration createPsi(@NotNull OverridePropertyStubElement stub) {
        return new LSFOverridePropertyStatementImpl(stub, this);
    }

    @Override
    public OverridePropertyStubElement createStub(@NotNull LSFOverridePropertyDeclaration psi, StubElement parentStub) {
        return new OverridePropertyStubImpl(parentStub, psi);
    }

    @Override
    public OverridePropertyStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new OverridePropertyStubImpl(dataStream, parentStub, this);
    }
}