package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;
import com.lsfusion.lang.psi.impl.LSFTableStatementImpl;
import com.lsfusion.lang.psi.stubs.TableStubElement;
import com.lsfusion.lang.psi.stubs.impl.TableStubImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.indexes.TableIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TableStubElementType extends FullNameStubElementType<TableStubElement, LSFTableDeclaration> {

    public TableStubElementType() {
        super("TABLE");
    }

    @Override
    public StringStubIndexExtension<LSFTableDeclaration> getGlobalIndex() {
        return TableIndex.getInstance();
    }

    @Override
    public StubIndexKey<String, LSFTableDeclaration> getGlobalIndexKey() {
        return LSFIndexKeys.TABLE;
    }

    @Override
    public LSFTableDeclaration createPsi(@NotNull TableStubElement stub) {
        return new LSFTableStatementImpl(stub, this);
    }

    @Override
    public TableStubElement createStub(@NotNull LSFTableDeclaration psi, StubElement parentStub) {
        return new TableStubImpl(parentStub, psi);
    }

    @Override
    public TableStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new TableStubImpl(dataStream, parentStub, this);
    }
}
