package com.simpleplugin.psi.stubs.types;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.simpleplugin.psi.declarations.LSFWindowDeclaration;
import com.simpleplugin.psi.impl.LSFWindowCreateStatementImpl;
import com.simpleplugin.psi.stubs.WindowStubElement;
import com.simpleplugin.psi.stubs.impl.WindowStubImpl;
import com.simpleplugin.psi.stubs.types.indexes.WindowIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class WindowStubElementType extends FullNameStubElementType<WindowStubElement, LSFWindowDeclaration> {

    public WindowStubElementType() {
        super("WINDOW");
    }

    @Override
    public StringStubIndexExtension<LSFWindowDeclaration> getGlobalIndex() {
        return WindowIndex.getInstance();
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
