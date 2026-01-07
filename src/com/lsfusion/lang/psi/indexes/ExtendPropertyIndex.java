package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFPropertyExtend;
import org.jetbrains.annotations.NotNull;

public class ExtendPropertyIndex extends ExtendActionOrPropIndex<LSFPropertyExtend> {
    private static final ExtendPropertyIndex ourInstance = new ExtendPropertyIndex();

    public static ExtendPropertyIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFPropertyExtend> getKey() {
        return LSFIndexKeys.EXTENDPROPERTY;
    }

    @Override
    protected Class<LSFPropertyExtend> getPsiClass() {
        return LSFPropertyExtend.class;
    }
}