package com.lsfusion.psi.stubs;

import com.intellij.psi.stubs.StubOutputStream;
import com.lsfusion.psi.LSFGlobalElement;
import com.lsfusion.psi.LSFStubElement;

import java.io.IOException;

public interface GlobalStubElement<This extends GlobalStubElement<This, Decl>, Decl extends LSFGlobalElement<Decl, This>> extends LSFStubElement<Decl> {

    public void serialize(StubOutputStream dataStream) throws IOException;

    public String getGlobalName();
}
