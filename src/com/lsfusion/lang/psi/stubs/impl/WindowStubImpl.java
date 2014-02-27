package com.lsfusion.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.psi.declarations.LSFWindowDeclaration;
import com.lsfusion.psi.stubs.WindowStubElement;
import com.lsfusion.psi.stubs.types.WindowStubElementType;

import java.io.IOException;

public class WindowStubImpl extends FullNameStubImpl<WindowStubElement, LSFWindowDeclaration> implements WindowStubElement {

    public WindowStubImpl(StubElement parent, LSFWindowDeclaration psi) {
        super(parent, psi);
    }

    public WindowStubImpl(StubInputStream dataStream, StubElement parentStub, WindowStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
