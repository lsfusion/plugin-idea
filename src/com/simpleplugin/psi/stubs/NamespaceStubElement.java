package com.simpleplugin.psi.stubs;

import com.simpleplugin.psi.declarations.LSFNamespaceDeclaration;

public interface NamespaceStubElement<This extends NamespaceStubElement<This, Decl>, Decl extends LSFNamespaceDeclaration<Decl, This>> extends GlobalStubElement<This, Decl> {
}
