package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import org.jetbrains.annotations.NotNull;

public class PropIndex extends StringStubIndexExtension<LSFGlobalPropDeclaration> {

    private static final PropIndex ourInstance = new PropIndex();

    public static PropIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFGlobalPropDeclaration> getKey() {
        return LSFIndexKeys.PROP;
    }
}
