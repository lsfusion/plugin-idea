package com.lsfusion.psi.stubs.types;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.psi.impl.LSFPropertyStatementImpl;
import com.lsfusion.psi.stubs.PropStubElement;
import com.lsfusion.psi.stubs.impl.PropStubImpl;
import com.lsfusion.psi.stubs.types.indexes.PropIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PropStubElementType extends FullNameStubElementType<PropStubElement, LSFGlobalPropDeclaration> {

    public PropStubElementType() {
        super("PROP");
    }

    @Override
    public StringStubIndexExtension<LSFGlobalPropDeclaration> getGlobalIndex() {
        return PropIndex.getInstance();
    }

    @Override
    public LSFGlobalPropDeclaration createPsi(@NotNull PropStubElement stub) {
        return new LSFPropertyStatementImpl(stub, this);
    }

    @Override
    public PropStubElement createStub(@NotNull LSFGlobalPropDeclaration psi, StubElement parentStub) {
        return new PropStubImpl(parentStub, psi);
    }

    @Override
    public PropStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new PropStubImpl(dataStream, parentStub, this);
    }
}
