package com.lsfusion.lang.psi.stubs.interfaces.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class ImplicitValueIndex extends StringStubIndexExtension {
    private static final ImplicitValueIndex INSTANCE = new ImplicitValueIndex();

    public static ImplicitValueIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey getKey() {
        return LSFStubElementTypes.IMPLICIT_VALUE.key;
    }
}