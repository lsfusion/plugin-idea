package com.lsfusion.lang.psi.stubs;

import com.lsfusion.lang.psi.declarations.LSFNamespaceDeclaration;

public interface NamespaceStubElement<This extends NamespaceStubElement<This, Decl>, Decl extends LSFNamespaceDeclaration<Decl, This>> extends GlobalStubElement<This, Decl> {
}
