package com.simpleplugin.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.simpleplugin.psi.declarations.LSFTableDeclaration;
import com.simpleplugin.psi.stubs.TableStubElement;
import com.simpleplugin.psi.stubs.types.TableStubElementType;

import java.io.IOException;

public class TableStubImpl extends FullNameStubImpl<TableStubElement, LSFTableDeclaration> implements TableStubElement {

    public TableStubImpl(StubElement parent, LSFTableDeclaration psi) {
        super(parent, psi);
    }

    public TableStubImpl(StubInputStream dataStream, StubElement parentStub, TableStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
