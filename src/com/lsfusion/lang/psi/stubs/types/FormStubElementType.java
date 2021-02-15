package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.indexes.LSFStringStubIndex;
import com.lsfusion.lang.psi.stubs.FormStubElement;
import com.lsfusion.lang.psi.stubs.impl.FormStubImpl;
import com.lsfusion.lang.psi.indexes.FormIndex;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class FormStubElementType extends FullNameStubElementType<FormStubElement, LSFFormDeclaration> {

    public FormStubElementType() {
        super("FORM");
    }

    @Override
    public LSFStringStubIndex<LSFFormDeclaration> getGlobalIndex() {
        return FormIndex.getInstance();
    }

    @Override
    public StubIndexKey<String, LSFFormDeclaration> getGlobalIndexKey() {
        return LSFIndexKeys.FORM;
    }

    @Override
    public LSFFormDeclaration createPsi(@NotNull FormStubElement stub) {
        return new com.lsfusion.lang.psi.impl.LSFFormDeclImpl(stub, this);
    }

    @Override
    public FormStubElement createStub(@NotNull LSFFormDeclaration psi, StubElement parentStub) {
        return new FormStubImpl(parentStub, psi);
    }

    @Override
    public FormStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new FormStubImpl(dataStream, parentStub, this);
    }
}
