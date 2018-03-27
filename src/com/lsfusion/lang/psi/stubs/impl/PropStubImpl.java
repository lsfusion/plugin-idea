package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.stubs.PropStubElement;
import com.lsfusion.lang.psi.stubs.types.PropStubElementType;

import java.io.IOException;

public class PropStubImpl extends ActionOrPropStubImpl<PropStubElement, LSFGlobalPropDeclaration> implements PropStubElement {

    public PropStubImpl(StubElement parent, LSFGlobalPropDeclaration psi) {
        super(parent, psi);
    }

    public PropStubImpl(StubInputStream dataStream, StubElement parentStub, PropStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
