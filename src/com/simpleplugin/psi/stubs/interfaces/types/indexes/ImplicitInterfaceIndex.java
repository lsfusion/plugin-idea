package com.simpleplugin.psi.stubs.interfaces.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class ImplicitInterfaceIndex extends StringStubIndexExtension {
    private static final ImplicitInterfaceIndex INSTANCE = new ImplicitInterfaceIndex();

    public static ImplicitInterfaceIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey getKey() {
        return LSFStubElementTypes.IMPLICIT_INTERFACE.key;
    }
}
