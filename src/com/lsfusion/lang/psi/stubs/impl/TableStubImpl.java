package com.lsfusion.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.psi.declarations.LSFTableDeclaration;
import com.lsfusion.psi.stubs.TableStubElement;
import com.lsfusion.psi.stubs.types.TableStubElementType;

import java.io.IOException;

public class TableStubImpl extends FullNameStubImpl<TableStubElement, LSFTableDeclaration> implements TableStubElement {

    public TableStubImpl(StubElement parent, LSFTableDeclaration psi) {
        super(parent, psi);
    }

    public TableStubImpl(StubInputStream dataStream, StubElement parentStub, TableStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
