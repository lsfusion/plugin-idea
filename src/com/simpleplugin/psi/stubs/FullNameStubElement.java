package com.simpleplugin.psi.stubs;

import com.simpleplugin.psi.declarations.LSFFullNameDeclaration;
import com.simpleplugin.psi.declarations.LSFGlobalDeclaration;

public interface FullNameStubElement<This extends FullNameStubElement<This, Decl>, Decl extends LSFFullNameDeclaration<Decl, This>> extends GlobalStubElement<This, Decl> {
}
