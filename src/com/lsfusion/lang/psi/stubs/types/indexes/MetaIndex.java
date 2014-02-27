package com.lsfusion.psi.stubs.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.psi.declarations.LSFMetaDeclaration;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class MetaIndex extends StringStubIndexExtension<LSFMetaDeclaration> {

    private static final MetaIndex ourInstance = new MetaIndex();
    public static MetaIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFMetaDeclaration> getKey() {
        return LSFStubElementTypes.META.key;
    }
}
