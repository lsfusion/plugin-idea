package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFImplicitValuePropStatement;
import org.jetbrains.annotations.NotNull;

public class ImplicitValueIndex extends StringStubIndexExtension<LSFImplicitValuePropStatement> {
    private static final ImplicitValueIndex INSTANCE = new ImplicitValueIndex();

    public static ImplicitValueIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFImplicitValuePropStatement> getKey() {
        return LSFIndexKeys.IMPLICIT_VALUE;
    }
}
