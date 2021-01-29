package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import org.jetbrains.annotations.NotNull;

public class ExtendFormIndex extends LSFStringStubIndex<LSFFormExtend> {

    private static final ExtendFormIndex ourInstance = new ExtendFormIndex();

    public static ExtendFormIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFFormExtend> getKey() {
        return LSFIndexKeys.EXTENDFORM;
    }

    @Override
    protected Class<LSFFormExtend> getPsiClass() {
        return LSFFormExtend.class;
    }
}

