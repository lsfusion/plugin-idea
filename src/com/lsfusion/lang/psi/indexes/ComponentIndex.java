package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFComponentStubDeclaration;
import org.jetbrains.annotations.NotNull;

public class ComponentIndex extends LSFStringStubIndex<LSFComponentStubDeclaration> {
    private static final ComponentIndex INSTANCE = new ComponentIndex();

    public static ComponentIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFComponentStubDeclaration> getKey() {
        return LSFIndexKeys.COMPONENT;
    }

    @Override
    protected Class<LSFComponentStubDeclaration> getPsiClass() {
        return LSFComponentStubDeclaration.class;
    }
}
