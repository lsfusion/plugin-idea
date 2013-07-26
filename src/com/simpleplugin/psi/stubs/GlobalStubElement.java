package com.simpleplugin.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubOutputStream;
import com.simpleplugin.psi.declarations.LSFGlobalDeclaration;
import com.simpleplugin.psi.declarations.LSFNamespaceDeclaration;

import java.io.IOException;

public interface GlobalStubElement<This extends GlobalStubElement<This, Decl>, Decl extends LSFGlobalDeclaration<Decl, This>> extends StubElement<Decl> {

    public void serialize(StubOutputStream dataStream) throws IOException;

    public String getGlobalName();
}
