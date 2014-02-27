package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFExplicitNamespaceDeclaration;
import com.lsfusion.lang.psi.stubs.ExplicitNamespaceStubElement;
import com.lsfusion.lang.psi.stubs.types.ExplicitNamespaceStubElementType;

import java.io.IOException;

public class ExplicitNamespaceStubImpl extends NamespaceStubImpl<ExplicitNamespaceStubElement, LSFExplicitNamespaceDeclaration> implements ExplicitNamespaceStubElement {

    public ExplicitNamespaceStubImpl(StubElement parent, LSFExplicitNamespaceDeclaration psi) {
        super(parent, psi);
    }

    public ExplicitNamespaceStubImpl(StubInputStream dataStream, StubElement parentStub, ExplicitNamespaceStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
