package com.simpleplugin.psi.stubs.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.simpleplugin.psi.declarations.LSFFormDeclaration;
import com.simpleplugin.psi.declarations.LSFTableDeclaration;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class TableIndex extends StringStubIndexExtension<LSFTableDeclaration> {

    private static final TableIndex ourInstance = new TableIndex();
    public static TableIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFTableDeclaration> getKey() {
        return LSFStubElementTypes.TABLE.key;
    }
}

