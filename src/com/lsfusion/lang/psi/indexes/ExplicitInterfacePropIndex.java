package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import org.jetbrains.annotations.NotNull;

public class ExplicitInterfacePropIndex extends ExplicitInterfaceActionOrPropIndex<LSFExplicitInterfacePropStatement> {
    private static final ExplicitInterfacePropIndex INSTANCE = new ExplicitInterfacePropIndex();

    public static ExplicitInterfacePropIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFExplicitInterfacePropStatement> getKey() {
        return LSFIndexKeys.EXPLICIT_INTERFACE_PROP;
    }

    @Override
    protected Class<LSFExplicitInterfacePropStatement> getPsiClass() {
        return LSFExplicitInterfacePropStatement.class;
    }
}
