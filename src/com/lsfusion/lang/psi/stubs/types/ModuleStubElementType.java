package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.impl.LSFModuleHeaderImpl;
import com.lsfusion.lang.psi.stubs.ModuleStubElement;
import com.lsfusion.lang.psi.stubs.impl.ModuleStubImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.indexes.ModuleIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ModuleStubElementType extends NamespaceStubElementType<ModuleStubElement, LSFModuleDeclaration> {

    public ModuleStubElementType() {
        super("MODULE");
    }

    @Override
    public StringStubIndexExtension<LSFModuleDeclaration> getGlobalIndex() {
        return ModuleIndex.getInstance();
    }

    @Override
    public StubIndexKey<String, LSFModuleDeclaration> getGlobalIndexKey() {
        return LSFIndexKeys.MODULE;
    }

    public LSFModuleDeclaration createPsi(@NotNull ModuleStubElement stub) {
        return new LSFModuleHeaderImpl(stub, this);
    }

    public ModuleStubElement createStub(@NotNull LSFModuleDeclaration psi, StubElement parentStub) {
        return new ModuleStubImpl(parentStub, psi);
    }

    public ModuleStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ModuleStubImpl(dataStream, parentStub, this);
    }
}
