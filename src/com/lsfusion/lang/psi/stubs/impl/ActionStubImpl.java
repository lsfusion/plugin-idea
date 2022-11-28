package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.stubs.ActionOrPropStubElement;
import com.lsfusion.lang.psi.stubs.ActionStubElement;

import java.io.IOException;

//по аналогии с PropStubImpl
public abstract class ActionStubImpl<This extends ActionOrPropStubElement<This, Decl>, Decl extends LSFActionOrGlobalPropDeclaration<Decl, This>> extends ActionOrPropStubImpl<This, Decl> implements ActionStubElement<This, Decl> {

    public ActionStubImpl(StubElement parent, Decl psi) {
        super(parent, psi);
    }

    public ActionStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<This, Decl> type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
