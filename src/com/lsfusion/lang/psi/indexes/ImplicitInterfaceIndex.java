package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFImplicitInterfacePropStatement;
import org.jetbrains.annotations.NotNull;

public class ImplicitInterfaceIndex extends StringStubIndexExtension<LSFImplicitInterfacePropStatement> {
    private static final ImplicitInterfaceIndex INSTANCE = new ImplicitInterfaceIndex();

    public static ImplicitInterfaceIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFImplicitInterfacePropStatement> getKey() {
        return LSFIndexKeys.IMPLICIT_INTERFACE;
    }
}
