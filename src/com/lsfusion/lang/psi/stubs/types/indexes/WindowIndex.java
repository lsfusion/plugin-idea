package com.lsfusion.psi.stubs.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.psi.declarations.LSFWindowDeclaration;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class WindowIndex extends StringStubIndexExtension<LSFWindowDeclaration> {

    private static final WindowIndex ourInstance = new WindowIndex();
    public static WindowIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFWindowDeclaration> getKey() {
        return LSFStubElementTypes.WINDOW.key;
    }
}

