package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionStatement;
import org.jetbrains.annotations.NotNull;

public class ExplicitInterfaceActionIndex extends ExplicitInterfaceActionOrPropIndex<LSFExplicitInterfaceActionStatement> {
    private static final ExplicitInterfaceActionIndex INSTANCE = new ExplicitInterfaceActionIndex();

    public static ExplicitInterfaceActionIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFExplicitInterfaceActionStatement> getKey() {
        return LSFIndexKeys.EXPLICIT_INTERFACE_ACTION;
    }

    @Override
    protected Class<LSFExplicitInterfaceActionStatement> getPsiClass() {
        return LSFExplicitInterfaceActionStatement.class;
    }
}