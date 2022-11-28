package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFBaseEventActionDeclaration;
import com.lsfusion.lang.psi.impl.LSFBaseEventPEImpl;
import com.lsfusion.lang.psi.stubs.BaseEventActionStubElement;
import com.lsfusion.lang.psi.stubs.impl.BaseEventActionStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class BaseEventActionStubElementType extends ActStubElementType<BaseEventActionStubElement, LSFBaseEventActionDeclaration> {

    public BaseEventActionStubElementType() {
        super("BASEEVENTACTION");
    }

    @Override
    public LSFBaseEventActionDeclaration createPsi(@NotNull BaseEventActionStubElement stub) {
        return new LSFBaseEventPEImpl(stub, this);
    }

    @NotNull
    @Override
    public BaseEventActionStubElement createStub(@NotNull LSFBaseEventActionDeclaration psi, StubElement parentStub) {
        return new BaseEventActionStubImpl(parentStub, psi);
    }

    @Override
    @NotNull
    public BaseEventActionStubElement deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new BaseEventActionStubImpl(dataStream, parentStub, this);
    }
}
