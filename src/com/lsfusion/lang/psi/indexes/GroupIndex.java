package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFGroupDeclaration;
import org.jetbrains.annotations.NotNull;

public class GroupIndex extends StringStubIndexExtension<LSFGroupDeclaration> {

    private static final GroupIndex ourInstance = new GroupIndex();

    public static GroupIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFGroupDeclaration> getKey() {
        return LSFIndexKeys.GROUP;
    }
}
