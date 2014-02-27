package com.lsfusion.psi.stubs.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class ModuleIndex extends StringStubIndexExtension<LSFModuleDeclaration> {

    private static final ModuleIndex ourInstance = new ModuleIndex();
    public static ModuleIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFModuleDeclaration> getKey() {
        return LSFStubElementTypes.MODULE.key;
    }
}
