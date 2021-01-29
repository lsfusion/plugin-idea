package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import org.jetbrains.annotations.NotNull;

public class ClassExtendsClassIndex extends LSFStringStubIndex<LSFClassExtend> {

    private static final ClassExtendsClassIndex ourInstance = new ClassExtendsClassIndex();

    public static ClassExtendsClassIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFClassExtend> getKey() {
        return LSFIndexKeys.EXTENDCLASS_SHORT;
    }

    @Override
    protected Class<LSFClassExtend> getPsiClass() {
        return LSFClassExtend.class;
    }
}
