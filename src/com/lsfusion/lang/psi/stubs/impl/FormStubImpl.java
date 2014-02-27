package com.lsfusion.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.psi.declarations.LSFFormDeclaration;
import com.lsfusion.psi.stubs.FormStubElement;
import com.lsfusion.psi.stubs.types.FormStubElementType;

import java.io.IOException;

public class FormStubImpl extends FullNameStubImpl<FormStubElement, LSFFormDeclaration> implements FormStubElement {

    public FormStubImpl(StubElement parent, LSFFormDeclaration psi) {
        super(parent, psi);
    }

    public FormStubImpl(StubInputStream dataStream, StubElement parentStub, FormStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
