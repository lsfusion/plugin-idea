package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFExplicitNamespaceDeclaration;
import com.lsfusion.lang.psi.impl.LSFNamespaceNameImpl;
import com.lsfusion.lang.psi.stubs.ExplicitNamespaceStubElement;
import com.lsfusion.lang.psi.stubs.impl.ExplicitNamespaceStubImpl;
import com.lsfusion.lang.psi.indexes.ExplicitNamespaceIndex;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ExplicitNamespaceStubElementType extends NamespaceStubElementType<ExplicitNamespaceStubElement, LSFExplicitNamespaceDeclaration> {

    public ExplicitNamespaceStubElementType() {
        super("EXPL_NAMESPACE");
    }

    @Override
    public StringStubIndexExtension<LSFExplicitNamespaceDeclaration> getGlobalIndex() {
        return ExplicitNamespaceIndex.getInstance();
    }

    @Override
    public StubIndexKey<String, LSFExplicitNamespaceDeclaration> getGlobalIndexKey() {
        return LSFIndexKeys.EXPLICIT_NAMESPACE;
    }

    @Override
    public LSFExplicitNamespaceDeclaration createPsi(@NotNull ExplicitNamespaceStubElement stub) {
        return new LSFNamespaceNameImpl(stub, this);
    }

    @Override
    public ExplicitNamespaceStubElement createStub(@NotNull LSFExplicitNamespaceDeclaration psi, StubElement parentStub) {
        return new ExplicitNamespaceStubImpl(parentStub, psi);
    }

    @NotNull
    @Override
    public ExplicitNamespaceStubElement deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ExplicitNamespaceStubImpl(dataStream, parentStub, this);
    }
}
