package com.simpleplugin.psi.stubs.types;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.simpleplugin.psi.declarations.LSFModuleDeclaration;
import com.simpleplugin.psi.impl.LSFModuleHeaderImpl;
import com.simpleplugin.psi.stubs.ModuleStubElement;
import com.simpleplugin.psi.stubs.impl.ModuleStubImpl;
import com.simpleplugin.psi.stubs.types.indexes.ModuleIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ModuleStubElementType extends NamespaceStubElementType<ModuleStubElement, LSFModuleDeclaration> {

    public ModuleStubElementType() {
        super("MODULE");
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

    @Override
    public StringStubIndexExtension<LSFModuleDeclaration> getGlobalIndex() {
        return ModuleIndex.getInstance();
    }
}
