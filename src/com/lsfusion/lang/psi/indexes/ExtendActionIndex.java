package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFActionExtend;
import org.jetbrains.annotations.NotNull;

public class ExtendActionIndex extends ExtendActionOrPropIndex<LSFActionExtend> {
    private static final ExtendActionIndex ourInstance = new ExtendActionIndex();

    public static ExtendActionIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFActionExtend> getKey() {
        return LSFIndexKeys.EXTENDACTION;
    }

    @Override
    protected Class<LSFActionExtend> getPsiClass() {
        return LSFActionExtend.class;
    }
}