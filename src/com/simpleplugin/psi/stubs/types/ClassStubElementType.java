package com.simpleplugin.psi.stubs.types;

import com.intellij.lang.Language;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.impl.LSFClassDeclImpl;
import com.simpleplugin.psi.impl.LSFClassStatementImpl;
import com.simpleplugin.psi.stubs.ClassStubElement;
import com.simpleplugin.psi.stubs.impl.ClassStubImpl;
import com.simpleplugin.psi.stubs.types.indexes.ClassIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public ClassStubElement createStub(@NotNull LSFClassDeclaration psi, StubElement parentStub) {
        return new ClassStubImpl(parentStub, psi);
    }

    @Override
    public ClassStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ClassStubImpl(dataStream, parentStub, this);
    }

    @Override
    public StringStubIndexExtension<LSFClassDeclaration> getGlobalIndex() {
        return ClassIndex.getInstance();
    }
}
