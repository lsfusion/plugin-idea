package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;
import org.jetbrains.annotations.NotNull;

public class TableIndex extends LSFStringStubIndex<LSFTableDeclaration> {

    private static final TableIndex ourInstance = new TableIndex();

    public static TableIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFTableDeclaration> getKey() {
        return LSFIndexKeys.TABLE;
    }

    @Override
    protected Class<LSFTableDeclaration> getPsiClass() {
        return LSFTableDeclaration.class;
    }
}

