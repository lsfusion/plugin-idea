package com.simpleplugin.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.simpleplugin.psi.declarations.LSFExplicitNamespaceDeclaration;
import com.simpleplugin.psi.stubs.ExplicitNamespaceStubElement;
import com.simpleplugin.psi.stubs.types.ExplicitNamespaceStubElementType;

import java.io.IOException;

public class ExplicitNamespaceStubImpl extends NamespaceStubImpl<ExplicitNamespaceStubElement, LSFExplicitNamespaceDeclaration> implements ExplicitNamespaceStubElement {

    public ExplicitNamespaceStubImpl(StubElement parent, LSFExplicitNamespaceDeclaration psi) {
        super(parent, psi);
    }

    public ExplicitNamespaceStubImpl(StubInputStream dataStream, StubElement parentStub, ExplicitNamespaceStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
