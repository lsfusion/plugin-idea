package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import org.jetbrains.annotations.NotNull;

public class ExtendClassIndex extends LSFStringStubIndex<LSFClassExtend> {

    private static final ExtendClassIndex ourInstance = new ExtendClassIndex();

    public static ExtendClassIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFClassExtend> getKey() {
        return LSFIndexKeys.EXTENDCLASS;
    }

    @Override
    protected Class<LSFClassExtend> getPsiClass() {
        return LSFClassExtend.class;
    }
}
