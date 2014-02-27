package com.lsfusion.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.psi.declarations.LSFGroupDeclaration;
import com.lsfusion.psi.stubs.GroupStubElement;
import com.lsfusion.psi.stubs.types.GroupStubElementType;

import java.io.IOException;

public class GroupStubImpl extends FullNameStubImpl<GroupStubElement, LSFGroupDeclaration> implements GroupStubElement {

    public GroupStubImpl(StubElement parent, LSFGroupDeclaration psi) {
        super(parent, psi);
    }

    public GroupStubImpl(StubInputStream dataStream, StubElement parentStub, GroupStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
