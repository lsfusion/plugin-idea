package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import org.jetbrains.annotations.NotNull;

public class ModuleIndex extends LSFStringStubIndex<LSFModuleDeclaration> {

    private static final ModuleIndex ourInstance = new ModuleIndex();

    public static ModuleIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFModuleDeclaration> getKey() {
        return LSFIndexKeys.MODULE;
    }

    @Override
    protected Class<LSFModuleDeclaration> getPsiClass() {
        return LSFModuleDeclaration.class;
    }
}
