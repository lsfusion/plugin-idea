package com.simpleplugin.psi.stubs.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.simpleplugin.psi.declarations.LSFGroupDeclaration;
import com.simpleplugin.psi.declarations.LSFTableDeclaration;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class GroupIndex extends StringStubIndexExtension<LSFGroupDeclaration> {

    private static final GroupIndex ourInstance = new GroupIndex();
    public static GroupIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFGroupDeclaration> getKey() {
        return LSFStubElementTypes.GROUP.key;
    }
}
