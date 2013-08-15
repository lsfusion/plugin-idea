package com.simpleplugin.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.simpleplugin.psi.declarations.LSFGlobalPropDeclaration;
import com.simpleplugin.psi.stubs.PropStubElement;
import com.simpleplugin.psi.stubs.types.PropStubElementType;

import java.io.IOException;

public class PropStubImpl extends FullNameStubImpl<PropStubElement, LSFGlobalPropDeclaration> implements PropStubElement {

    public PropStubImpl(StubElement parent, LSFGlobalPropDeclaration psi) {
        super(parent, psi);
    }

    public PropStubImpl(StubInputStream dataStream, StubElement parentStub, PropStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
