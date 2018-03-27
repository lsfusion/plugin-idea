package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.stubs.ActionOrPropStubElement;
import com.lsfusion.lang.psi.stubs.FullNameStubElement;

import java.io.IOException;

public abstract class ActionOrPropStubImpl<This extends ActionOrPropStubElement<This, Decl>, Decl extends LSFActionOrGlobalPropDeclaration<Decl, This>> extends FullNameStubImpl<This, Decl> implements ActionOrPropStubElement<This, Decl> {

    private final boolean isNoParams;
    
    public ActionOrPropStubImpl(StubElement parent, Decl psi) {
        super(parent, psi);
        isNoParams = psi.isNoParams();
    }

    public ActionOrPropStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<This, Decl> type) throws IOException {
        super(dataStream, parentStub, type);
        isNoParams = dataStream.readBoolean();
    }

    @Override
    public void serialize(StubOutputStream dataStream) throws IOException {
        super.serialize(dataStream);
        dataStream.writeBoolean(isNoParams);
    }

    public ActionOrPropStubImpl(StubElement parent, IStubElementType elementType, StringRef name, boolean isNoParams) {
        super(parent, elementType, name);
        this.isNoParams = isNoParams;
    }

    public boolean isNoParams() {
        return isNoParams;
    }
}
