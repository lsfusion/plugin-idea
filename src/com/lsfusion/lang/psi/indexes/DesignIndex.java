package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.extend.LSFDesign;
import org.jetbrains.annotations.NotNull;

public class DesignIndex extends LSFStringStubIndex<LSFDesign> {

    private static final DesignIndex ourInstance = new DesignIndex();

    public static DesignIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFDesign> getKey() {
        return LSFIndexKeys.DESIGN;
    }

    @Override
    protected Class<LSFDesign> getPsiClass() {
        return LSFDesign.class;
    }
}