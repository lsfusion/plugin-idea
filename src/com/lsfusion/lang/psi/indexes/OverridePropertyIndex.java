package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFOverridePropertyDeclaration;
import org.jetbrains.annotations.NotNull;

public class OverridePropertyIndex extends LSFStringStubIndex<LSFOverridePropertyDeclaration> {
    private static final OverridePropertyIndex ourInstance = new OverridePropertyIndex();

    public static OverridePropertyIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFOverridePropertyDeclaration> getKey() {
        return LSFIndexKeys.OVERRIDEPROPERTY;
    }

    @Override
    protected Class<LSFOverridePropertyDeclaration> getPsiClass() {
        return LSFOverridePropertyDeclaration.class;
    }
}