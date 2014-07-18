package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFGroupDeclaration;
import com.lsfusion.lang.psi.impl.LSFGroupStatementImpl;
import com.lsfusion.lang.psi.stubs.GroupStubElement;
import com.lsfusion.lang.psi.stubs.impl.GroupStubImpl;
import com.lsfusion.lang.psi.indexes.GroupIndex;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
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
    public StubIndexKey<String, LSFGroupDeclaration> getGlobalIndexKey() {
        return LSFIndexKeys.GROUP;
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
