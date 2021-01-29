package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.impl.LSFClassDeclImpl;
import com.lsfusion.lang.psi.indexes.LSFStringStubIndex;
import com.lsfusion.lang.psi.stubs.ClassStubElement;
import com.lsfusion.lang.psi.stubs.impl.ClassStubImpl;
import com.lsfusion.lang.psi.indexes.ClassIndex;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ClassStubElementType extends FullNameStubElementType<ClassStubElement, LSFClassDeclaration> {

    public ClassStubElementType() {
        super("CLASS");
    }

    @Override
    public LSFClassDeclaration createPsi(@NotNull ClassStubElement stub) {
        return new LSFClassDeclImpl(stub, this);
    }

    @Override
    public StubIndexKey<String, LSFClassDeclaration> getGlobalIndexKey() {
        return LSFIndexKeys.CLASS;
    }

    @Override
    public ClassStubElement createStub(@NotNull LSFClassDeclaration psi, StubElement parentStub) {
        return new ClassStubImpl(parentStub, psi);
    }

    @Override
    public ClassStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ClassStubImpl(dataStream, parentStub, this);
    }

    @Override
    public LSFStringStubIndex<LSFClassDeclaration> getGlobalIndex() {
        return ClassIndex.getInstance();
    }
}
