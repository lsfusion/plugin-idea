package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.LSFInterfacePropStatement;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceProp;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfacePropStatement;
import org.jetbrains.annotations.NotNull;

public class ExplicitInterfacePropIndex extends ExplicitInterfaceActionOrPropIndex<LSFExplicitInterfaceProp> {
    private static final ExplicitInterfacePropIndex INSTANCE = new ExplicitInterfacePropIndex();

    public static ExplicitInterfacePropIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFExplicitInterfaceProp> getKey() {
        return LSFIndexKeys.EXPLICIT_INTERFACE_PROP;
    }

    @Override
    protected Class<LSFExplicitInterfaceProp> getPsiClass() {
        return LSFExplicitInterfaceProp.class;
    }
}
