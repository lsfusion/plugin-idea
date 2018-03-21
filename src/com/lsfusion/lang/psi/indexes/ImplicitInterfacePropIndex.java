package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFImplicitInterfacePropStatement;
import org.jetbrains.annotations.NotNull;

public class ImplicitInterfacePropIndex extends LSFStringStubIndex<LSFImplicitInterfacePropStatement> {
    private static final ImplicitInterfacePropIndex INSTANCE = new ImplicitInterfacePropIndex();

    public static ImplicitInterfacePropIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFImplicitInterfacePropStatement> getKey() {
        return LSFIndexKeys.IMPLICIT_INTERFACE;
    }

    @Override
    protected Class<LSFImplicitInterfacePropStatement> getPsiClass() {
        return LSFImplicitInterfacePropStatement.class;
    }
}
