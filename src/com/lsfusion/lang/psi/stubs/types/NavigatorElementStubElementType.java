package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFNavigatorElementDeclaration;
import com.lsfusion.lang.psi.impl.LSFNewNavigatorElementStatementImpl;
import com.lsfusion.lang.psi.stubs.NavigatorElementStubElement;
import com.lsfusion.lang.psi.stubs.impl.NavigatorElementStubImpl;
import com.lsfusion.lang.psi.stubs.types.indexes.NavigatorElementIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class NavigatorElementStubElementType extends FullNameStubElementType<NavigatorElementStubElement, LSFNavigatorElementDeclaration> {

    public NavigatorElementStubElementType() {
        super("NAVIGATOR_ELEMENT");
    }

    @Override
    public StringStubIndexExtension<LSFNavigatorElementDeclaration> getGlobalIndex() {
        return NavigatorElementIndex.getInstance();
    }

    @Override
    public LSFNavigatorElementDeclaration createPsi(@NotNull NavigatorElementStubElement stub) {
        return new LSFNewNavigatorElementStatementImpl(stub, this);
    }

    @Override
    public NavigatorElementStubElement createStub(@NotNull LSFNavigatorElementDeclaration psi, StubElement parentStub) {
        return new NavigatorElementStubImpl(parentStub, psi);
    }

    @Override
    public NavigatorElementStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new NavigatorElementStubImpl(dataStream, parentStub, this);
    }
}
