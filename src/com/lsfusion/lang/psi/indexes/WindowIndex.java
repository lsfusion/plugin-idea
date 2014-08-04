package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFWindowDeclaration;
import org.jetbrains.annotations.NotNull;

public class WindowIndex extends StringStubIndexExtension<LSFWindowDeclaration> {

    private static final WindowIndex ourInstance = new WindowIndex();

    public static WindowIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFWindowDeclaration> getKey() {
        return LSFIndexKeys.WINDOW;
    }
}
