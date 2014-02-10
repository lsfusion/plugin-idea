package com.simpleplugin.psi.stubs.interfaces.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class ExplicitInterfaceIndex extends StringStubIndexExtension {
    private static final ExplicitInterfaceIndex INSTANCE = new ExplicitInterfaceIndex();

    public static ExplicitInterfaceIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey getKey() {
        return LSFStubElementTypes.EXPLICIT_INTERFACE.key;
    }
}
