package com.simpleplugin.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.simpleplugin.psi.declarations.LSFModuleDeclaration;
import com.simpleplugin.psi.stubs.ModuleStubElement;
import com.simpleplugin.psi.stubs.types.ModuleStubElementType;

import java.io.IOException;

public class ModuleStubImpl extends NamespaceStubImpl<ModuleStubElement, LSFModuleDeclaration> implements ModuleStubElement {

    public ModuleStubImpl(StubElement parent, LSFModuleDeclaration psi) {
        super(parent, psi);
    }

    public ModuleStubImpl(StubInputStream dataStream, StubElement parentStub, ModuleStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
