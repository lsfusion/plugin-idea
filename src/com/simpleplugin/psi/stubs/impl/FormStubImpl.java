package com.simpleplugin.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.simpleplugin.psi.declarations.LSFFormDeclaration;
import com.simpleplugin.psi.stubs.FormStubElement;
import com.simpleplugin.psi.stubs.types.FormStubElementType;

import java.io.IOException;

public class FormStubImpl extends FullNameStubImpl<FormStubElement, LSFFormDeclaration> implements FormStubElement {

    public FormStubImpl(StubElement parent, LSFFormDeclaration psi) {
        super(parent, psi);
    }

    public FormStubImpl(StubInputStream dataStream, StubElement parentStub, FormStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
