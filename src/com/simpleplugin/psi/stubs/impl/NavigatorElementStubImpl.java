package com.simpleplugin.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.simpleplugin.psi.declarations.LSFNavigatorElementDeclaration;
import com.simpleplugin.psi.stubs.NavigatorElementStubElement;
import com.simpleplugin.psi.stubs.types.NavigatorElementStubElementType;

import java.io.IOException;

public class NavigatorElementStubImpl extends FullNameStubImpl<NavigatorElementStubElement, LSFNavigatorElementDeclaration> implements NavigatorElementStubElement {

    public NavigatorElementStubImpl(StubElement parent, LSFNavigatorElementDeclaration psi) {
        super(parent, psi);
    }

    public NavigatorElementStubImpl(StubInputStream dataStream, StubElement parentStub, NavigatorElementStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
