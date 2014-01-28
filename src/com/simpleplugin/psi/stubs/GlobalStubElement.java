package com.simpleplugin.psi.stubs;

import com.intellij.psi.stubs.StubOutputStream;
import com.simpleplugin.psi.LSFGlobalElement;
import com.simpleplugin.psi.LSFStubElement;

import java.io.IOException;

public interface GlobalStubElement<This extends GlobalStubElement<This, Decl>, Decl extends LSFGlobalElement<Decl, This>> extends LSFStubElement<Decl> {

    public void serialize(StubOutputStream dataStream) throws IOException;

    public String getGlobalName();
}
