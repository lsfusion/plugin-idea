package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFExplicitValuePropStatement;
import org.jetbrains.annotations.NotNull;

public class ExplicitValueIndex extends LSFStringStubIndex<LSFExplicitValuePropStatement> {
    private static final ExplicitValueIndex INSTANCE = new ExplicitValueIndex();

    public static ExplicitValueIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFExplicitValuePropStatement> getKey() {
        return LSFIndexKeys.EXPLICIT_VALUE;
    }

    @Override
    protected Class<LSFExplicitValuePropStatement> getPsiClass() {
        return LSFExplicitValuePropStatement.class;
    }
}
