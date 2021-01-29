package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFWindowDeclaration;
import com.lsfusion.lang.psi.impl.LSFWindowCreateStatementImpl;
import com.lsfusion.lang.psi.indexes.LSFStringStubIndex;
import com.lsfusion.lang.psi.stubs.WindowStubElement;
import com.lsfusion.lang.psi.stubs.impl.WindowStubImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.indexes.WindowIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class WindowStubElementType extends FullNameStubElementType<WindowStubElement, LSFWindowDeclaration> {

    public WindowStubElementType() {
        super("WINDOW");
    }

    @Override
    public LSFStringStubIndex<LSFWindowDeclaration> getGlobalIndex() {
        return WindowIndex.getInstance();
    }

    @Override
    public StubIndexKey<String, LSFWindowDeclaration> getGlobalIndexKey() {
        return LSFIndexKeys.WINDOW;
    }

    @Override
    public LSFWindowDeclaration createPsi(@NotNull WindowStubElement stub) {
        return new LSFWindowCreateStatementImpl(stub, this);
    }

    @Override
    public WindowStubElement createStub(@NotNull LSFWindowDeclaration psi, StubElement parentStub) {
        return new WindowStubImpl(parentStub, psi);
    }

    @Override
    public WindowStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new WindowStubImpl(dataStream, parentStub, this);
    }
}
