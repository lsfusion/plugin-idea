package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFMetaDeclaration;
import org.jetbrains.annotations.NotNull;

public class MetaIndex extends LSFStringStubIndex<LSFMetaDeclaration> {

    private static final MetaIndex ourInstance = new MetaIndex();

    public static MetaIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFMetaDeclaration> getKey() {
        return LSFIndexKeys.META;
    }

    @Override
    protected Class<LSFMetaDeclaration> getPsiClass() {
        return LSFMetaDeclaration.class;
    }
}
