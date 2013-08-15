package com.simpleplugin.psi.stubs.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.simpleplugin.psi.declarations.LSFGlobalPropDeclaration;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class PropIndex extends StringStubIndexExtension<LSFGlobalPropDeclaration> {

    private static final PropIndex ourInstance = new PropIndex();
    public static PropIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFGlobalPropDeclaration> getKey() {
        return LSFStubElementTypes.PROP.key;
    }
}
