package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;
import com.lsfusion.lang.psi.impl.LSFMetaCodeDeclarationStatementImpl;
import com.lsfusion.lang.psi.stubs.MetaStubElement;
import com.lsfusion.lang.psi.stubs.impl.MetaStubImpl;
import com.lsfusion.lang.psi.stubs.types.indexes.MetaIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MetaStubElementType extends FullNameStubElementType<MetaStubElement, LSFMetaDeclaration> {

    public MetaStubElementType() {
        super("META");
    }

    @Override
    public StringStubIndexExtension<LSFMetaDeclaration> getGlobalIndex() {
        return MetaIndex.getInstance();
    }

    @Override
    public LSFMetaDeclaration createPsi(@NotNull MetaStubElement stub) {
        return new LSFMetaCodeDeclarationStatementImpl(stub, this);
    }

    @Override
    public MetaStubElement createStub(@NotNull LSFMetaDeclaration psi, StubElement parentStub) {
        return new MetaStubImpl(parentStub, psi);
    }

    @Override
    public MetaStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new MetaStubImpl(dataStream, parentStub, this);
    }
}
