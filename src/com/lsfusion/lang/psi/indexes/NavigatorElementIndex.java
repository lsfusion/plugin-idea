package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFNavigatorElementDeclaration;
import org.jetbrains.annotations.NotNull;

public class NavigatorElementIndex extends LSFStringStubIndex<LSFNavigatorElementDeclaration> {

    private static final NavigatorElementIndex ourInstance = new NavigatorElementIndex();

    public static NavigatorElementIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFNavigatorElementDeclaration> getKey() {
        return LSFIndexKeys.NAVIGATORELEMENT;
    }

    @Override
    protected Class<LSFNavigatorElementDeclaration> getPsiClass() {
        return LSFNavigatorElementDeclaration.class;
    }
}

