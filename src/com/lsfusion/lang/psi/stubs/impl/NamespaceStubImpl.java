package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFNamespaceDeclaration;
import com.lsfusion.lang.psi.stubs.NamespaceStubElement;

import java.io.IOException;

public class NamespaceStubImpl<This extends NamespaceStubElement<This, Decl>, Decl extends LSFNamespaceDeclaration<Decl, This>> extends GlobalStubImpl<This, Decl> implements NamespaceStubElement<This, Decl> {

    public NamespaceStubImpl(StubElement parent, Decl psi) {
        super(parent, psi);
    }

    public NamespaceStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<This, Decl> type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
