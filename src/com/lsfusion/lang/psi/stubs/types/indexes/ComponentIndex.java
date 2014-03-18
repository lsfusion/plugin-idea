package com.lsfusion.lang.psi.stubs.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFComponentStubDeclaration;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class ComponentIndex extends StringStubIndexExtension<LSFComponentStubDeclaration> {
    private static final ComponentIndex INSTANCE = new ComponentIndex();

    public static ComponentIndex getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFComponentStubDeclaration> getKey() {
        return LSFStubElementTypes.COMPONENT.key;
    }
}
