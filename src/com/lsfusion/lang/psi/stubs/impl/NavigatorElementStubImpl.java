package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFNavigatorElementDeclaration;
import com.lsfusion.lang.psi.stubs.NavigatorElementStubElement;
import com.lsfusion.lang.psi.stubs.types.NavigatorElementStubElementType;

import java.io.IOException;

public class NavigatorElementStubImpl extends FullNameStubImpl<NavigatorElementStubElement, LSFNavigatorElementDeclaration> implements NavigatorElementStubElement {

    public NavigatorElementStubImpl(StubElement parent, LSFNavigatorElementDeclaration psi) {
        super(parent, psi);
    }

    public NavigatorElementStubImpl(StubInputStream dataStream, StubElement parentStub, NavigatorElementStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
