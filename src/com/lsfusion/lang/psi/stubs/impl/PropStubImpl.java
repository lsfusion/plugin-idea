package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.impl.LSFAggrParamGlobalPropDeclarationImpl;
import com.lsfusion.lang.psi.impl.LSFAggrParamPropDeclareImpl;
import com.lsfusion.lang.psi.impl.LSFPropertyStatementImpl;
import com.lsfusion.lang.psi.stubs.ActionOrPropStubElement;
import com.lsfusion.lang.psi.stubs.PropStubElement;
import com.lsfusion.lang.psi.stubs.types.PropStubElementType;

import java.io.IOException;

public abstract class PropStubImpl<This extends ActionOrPropStubElement<This, Decl>, Decl extends LSFActionOrGlobalPropDeclaration<Decl, This>> extends ActionOrPropStubImpl<This, Decl> implements PropStubElement<This, Decl> {

    public PropStubImpl(StubElement parent, Decl psi) {
        super(parent, psi);
    }

    public PropStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<This, Decl> type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
