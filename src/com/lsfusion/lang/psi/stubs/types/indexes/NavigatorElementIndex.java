package com.lsfusion.psi.stubs.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.psi.declarations.LSFNavigatorElementDeclaration;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class NavigatorElementIndex extends StringStubIndexExtension<LSFNavigatorElementDeclaration> {

    private static final NavigatorElementIndex ourInstance = new NavigatorElementIndex();
    public static NavigatorElementIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFNavigatorElementDeclaration> getKey() {
        return LSFStubElementTypes.NAVIGATORELEMENT.key;
    }
}

