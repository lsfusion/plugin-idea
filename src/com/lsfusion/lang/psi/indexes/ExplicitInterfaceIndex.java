package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import org.jetbrains.annotations.NotNull;

public class ExplicitInterfaceIndex extends StringStubIndexExtension<LSFExplicitInterfacePropStatement> {
    private static final ExplicitInterfaceIndex INSTANCE = new ExplicitInterfaceIndex();

    public static ExplicitInterfaceIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFExplicitInterfacePropStatement> getKey() {
        return LSFIndexKeys.EXPLICIT_INTERFACE;
    }
}
