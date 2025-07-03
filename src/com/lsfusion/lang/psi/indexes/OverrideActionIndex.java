package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFOverrideActionDeclaration;
import org.jetbrains.annotations.NotNull;

public class OverrideActionIndex extends LSFStringStubIndex<LSFOverrideActionDeclaration> {
    private static final OverrideActionIndex ourInstance = new OverrideActionIndex();

    public static OverrideActionIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFOverrideActionDeclaration> getKey() {
        return LSFIndexKeys.OVERRIDEACTION;
    }

    @Override
    protected Class<LSFOverrideActionDeclaration> getPsiClass() {
        return LSFOverrideActionDeclaration.class;
    }
}