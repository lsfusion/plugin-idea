package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFActionDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.stubs.ActionStubElement;
import com.lsfusion.lang.psi.stubs.PropStubElement;
import com.lsfusion.lang.psi.stubs.types.ActionStubElementType;
import com.lsfusion.lang.psi.stubs.types.PropStubElementType;

import java.io.IOException;

public class ActionStubImpl extends FullNameStubImpl<ActionStubElement, LSFActionDeclaration> implements ActionStubElement {

    public ActionStubImpl(StubElement parent, LSFActionDeclaration psi) {
        super(parent, psi);
    }

    public ActionStubImpl(StubInputStream dataStream, StubElement parentStub, ActionStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
