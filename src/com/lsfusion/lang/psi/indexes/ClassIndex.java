package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import org.jetbrains.annotations.NotNull;

public class ClassIndex extends StringStubIndexExtension<LSFClassDeclaration> {

    private static final ClassIndex ourInstance = new ClassIndex();

    public static ClassIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFClassDeclaration> getKey() {
        return LSFIndexKeys.CLASS;
    }
}
