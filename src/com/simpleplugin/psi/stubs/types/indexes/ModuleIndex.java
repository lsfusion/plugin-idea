package com.simpleplugin.psi.stubs.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.declarations.LSFModuleDeclaration;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
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
