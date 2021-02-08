package com.lsfusion.lang.psi;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubOutputStream;

import java.io.IOException;

public interface LSFStubElement<This extends LSFStubElement<This, Decl>, Decl extends LSFElement> extends StubElement<Decl> {

    void serialize(StubOutputStream dataStream) throws IOException;

    boolean isCorrect();

    boolean isInMetaDecl();

}
