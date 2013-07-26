package com.simpleplugin.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.simpleplugin.psi.declarations.LSFPropDeclaration;
import com.simpleplugin.psi.stubs.FullNameStubElement;
import com.simpleplugin.psi.stubs.PropStubElement;
import com.simpleplugin.psi.stubs.types.PropStubElementType;

import java.io.IOException;

public class PropStubImpl extends FullNameStubImpl<PropStubElement, LSFPropDeclaration> implements PropStubElement {

    public PropStubImpl(StubElement parent, LSFPropDeclaration psi) {
        super(parent, psi);
    }

    public PropStubImpl(StubInputStream dataStream, StubElement parentStub, PropStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
