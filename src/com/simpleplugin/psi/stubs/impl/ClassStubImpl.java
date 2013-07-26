package com.simpleplugin.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.stubs.ClassStubElement;
import com.simpleplugin.psi.stubs.types.ClassStubElementType;

import java.io.IOException;

public class ClassStubImpl extends FullNameStubImpl<ClassStubElement, LSFClassDeclaration> implements ClassStubElement {

    public ClassStubImpl(StubElement parent, LSFClassDeclaration psi) {
        super(parent, psi);
    }

    public ClassStubImpl(StubInputStream dataStream, StubElement parentStub, ClassStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
