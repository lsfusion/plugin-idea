package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFActionDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.impl.LSFActionStatementImpl;
import com.lsfusion.lang.psi.impl.LSFPropertyStatementImpl;
import com.lsfusion.lang.psi.indexes.ActionIndex;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.indexes.PropIndex;
import com.lsfusion.lang.psi.stubs.ActionStubElement;
import com.lsfusion.lang.psi.stubs.PropStubElement;
import com.lsfusion.lang.psi.stubs.impl.ActionStubImpl;
import com.lsfusion.lang.psi.stubs.impl.PropStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ActionStubElementType extends ActionOrPropStubElementType<ActionStubElement, LSFActionDeclaration> {

    public ActionStubElementType() {
        super("ACTION");
    }

    @Override
    public StringStubIndexExtension<LSFActionDeclaration> getGlobalIndex() {
        return ActionIndex.getInstance();
    }

    @Override
    public StubIndexKey<String, LSFActionDeclaration> getGlobalIndexKey() {
        return LSFIndexKeys.ACTION;
    }

    @Override
    public LSFActionDeclaration createPsi(@NotNull ActionStubElement stub) {
        return new LSFActionStatementImpl(stub, this);
    }

    @Override
    public ActionStubElement createStub(@NotNull LSFActionDeclaration psi, StubElement parentStub) {
        return new ActionStubImpl(parentStub, psi);
    }

    @Override
    public ActionStubElement deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ActionStubImpl(dataStream, parentStub, this);
    }

}
