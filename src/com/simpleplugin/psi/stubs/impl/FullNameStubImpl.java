package com.simpleplugin.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.util.io.StringRef;
import com.simpleplugin.psi.declarations.LSFFullNameDeclaration;
import com.simpleplugin.psi.stubs.FullNameStubElement;

import java.io.IOException;

public class FullNameStubImpl<This extends FullNameStubElement<This, Decl>, Decl extends LSFFullNameDeclaration<Decl, This>> extends GlobalStubImpl<This, Decl> implements FullNameStubElement<This, Decl> {

    public FullNameStubImpl(StubElement parent, Decl psi) {
        super(parent, psi);
    }

    public FullNameStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<This, Decl> type) throws IOException {
        super(dataStream, parentStub, type);
    }

    public FullNameStubImpl(StubElement parent, IStubElementType elementType, StringRef name) {
        super(parent, elementType, name);
    }
}
