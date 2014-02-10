package com.simpleplugin.psi.stubs.interfaces.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class ExplicitValueIndex extends StringStubIndexExtension {
    private static final ExplicitValueIndex INSTANCE = new ExplicitValueIndex();

    public static ExplicitValueIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey getKey() {
        return LSFStubElementTypes.EXPLICIT_VALUE.key;
    }
}
