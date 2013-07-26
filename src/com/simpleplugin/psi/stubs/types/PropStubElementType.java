package com.simpleplugin.psi.stubs.types;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.simpleplugin.psi.declarations.LSFPropDeclaration;
import com.simpleplugin.psi.impl.LSFPropertyStatementImpl;
import com.simpleplugin.psi.stubs.PropStubElement;
import com.simpleplugin.psi.stubs.impl.PropStubImpl;
import com.simpleplugin.psi.stubs.types.indexes.PropIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PropStubElementType extends FullNameStubElementType<PropStubElement, LSFPropDeclaration> {

    public PropStubElementType() {
        super("PROP");
    }

    @Override
    public StringStubIndexExtension<LSFPropDeclaration> getGlobalIndex() {
        return PropIndex.getInstance();
    }

    @Override
    public LSFPropDeclaration createPsi(@NotNull PropStubElement stub) {
        return new LSFPropertyStatementImpl(stub, this);
    }

    @Override
    public PropStubElement createStub(@NotNull LSFPropDeclaration psi, StubElement parentStub) {
        return new PropStubImpl(parentStub, psi);
    }

    @Override
    public PropStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new PropStubImpl(dataStream, parentStub, this);
    }
}
