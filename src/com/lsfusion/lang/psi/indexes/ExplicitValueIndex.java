package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFExplicitValuePropStatement;
import com.lsfusion.lang.psi.declarations.LSFExplicitValueProp;
import org.jetbrains.annotations.NotNull;

public class ExplicitValueIndex extends LSFStringStubIndex<LSFExplicitValueProp> {
    private static final ExplicitValueIndex INSTANCE = new ExplicitValueIndex();

    public static ExplicitValueIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFExplicitValueProp> getKey() {
        return LSFIndexKeys.EXPLICIT_VALUE;
    }

    @Override
    protected Class<LSFExplicitValueProp> getPsiClass() {
        return LSFExplicitValueProp.class;
    }
}
