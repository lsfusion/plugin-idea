package com.simpleplugin.psi.stubs.types;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.simpleplugin.psi.declarations.LSFGroupDeclaration;
import com.simpleplugin.psi.impl.LSFGroupStatementImpl;
import com.simpleplugin.psi.stubs.GroupStubElement;
import com.simpleplugin.psi.stubs.impl.GroupStubImpl;
import com.simpleplugin.psi.stubs.types.indexes.GroupIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GroupStubElementType extends FullNameStubElementType<GroupStubElement, LSFGroupDeclaration> {
    
    public GroupStubElementType() {
        super("GROUP");
    }

    @Override
    public StringStubIndexExtension<LSFGroupDeclaration> getGlobalIndex() {
        return GroupIndex.getInstance();
    }

    @Override
    public LSFGroupDeclaration createPsi(@NotNull GroupStubElement stub) {
        return new LSFGroupStatementImpl(stub, this);
    }

    @Override
    public GroupStubElement createStub(@NotNull LSFGroupDeclaration psi, StubElement parentStub) {
        return new GroupStubImpl(parentStub, psi);
    }

    @Override
    public GroupStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new GroupStubImpl(dataStream, parentStub, this);
    }
}
